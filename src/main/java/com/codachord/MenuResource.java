package com.codachord;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.codachord.Fruit.from;

@Path("menu")
public class MenuResource {

    @Inject
    MySQLPool client;

    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    boolean schemaCreate;

    private String test = "blah";
    @PostConstruct
    void config() {
        if (schemaCreate) {
            initdb();
        }
    }

 /*   @POST
    public Uni<Response> create(MenuCalendar menuCalendar) {
        return menuCalendar.save(client)
                .onItem().transform(id -> URI.create("/menu/" + id))
                .onItem().transform(uri -> Response.created(uri).build());
    }*/

    @POST
    @Path("item")
    public Uni<Response> create(MenuItem fruit) {
        log.info("Create menu item " + fruit);
        return fruit.save(client)
                .onItem().transform(id -> URI.create("/menu/" + id))
                .onItem().transform(uri -> Response.created(uri).build());
    }

    @GET
    @Path("calendar_start")
    public Uni<MenuCalendar> getCalendarStart() {
        return MenuCalendar.findById(client);
    }

    @GET
    public Multi<MenuItem> get() {
        return MenuItem.findAll(client);
    }

    public static Multi<Fruit> findAll(MySQLPool client) {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Fruit::from);
    }
/*
    public static Multi<Fruit> findAll(MySQLPool client) {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Fruit::from);
    }*/

    @GET
    @Path("{day}")
    public Uni<Response> getSingle(@PathParam("day") int id) {
        return MenuItem.findById(client, id)
                .onItem().transform(fruit -> fruit != null ? Response.ok(fruit) : Response.status(Status.NOT_FOUND))
                .onItem().transform(ResponseBuilder::build);
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return MenuItem.delete(client, id)
                .onItem().transform(deleted -> deleted ? Status.NO_CONTENT : Status.NOT_FOUND)
                .onItem().transform(status -> Response.status(status).build());
    }

    private void initdb() {
        Calendar today = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        log.info("DATE:   " +format1.format(today.getTime()) );
        client.query("DROP TABLE IF EXISTS menu_calendar").execute()
                .flatMap(r -> client.query("CREATE TABLE menu_calendar (     `id` enum('1'), start DATE, PRIMARY KEY (id) );").execute())
               //" + format1.format(today) + "
                .flatMap(r -> client.query("INSERT INTO menu_calendar (start) VALUES ('" + format1.format(today.getTime()) + "')").execute())
                .await().indefinitely();
    }

    private static final Logger log = Logger.getLogger(MenuResource.class);
}