package com.codachord;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;

@Path("menu")
public class MenuResource {

    @Inject
    MySQLPool client;

    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    boolean schemaCreate;

    @PostConstruct
    void config() {
        if (schemaCreate) {
            initdb();
        }
    }

    @POST
    public Uni<Response> create(MenuCalendar menuCalendar) {
        return menuCalendar.save(client)
                .onItem().transform(id -> URI.create("/menu/" + id))
                .onItem().transform(uri -> Response.created(uri).build());
    }
    
    @POST
    @Path("dinner")
    @Consumes(MediaType.APPLICATION_JSON)
    public Multi<Response> dinner(List<MenuItem> menuitems) {
    	return Menu.dinner.save(menuitems, client)
                .onItem().transform(id -> URI.create("/menu/dinner"))
                .onItem().transform(uri -> Response.created(uri).build());
    }
    
    @POST
    @Path("lunch")
    @Consumes(MediaType.APPLICATION_JSON)
    public Multi<Response> lunch(List<MenuItem> menuitems) {
    	return Menu.lunch.save(menuitems, client)
                .onItem().transform(id -> URI.create("/menu/lunch"))
                .onItem().transform(uri -> Response.created(uri).build());
    }

    @POST
    @Path("item")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
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
    @Path("lunch")
    public Multi<MenuItem> lunch() {
        return Menu.lunch.findAll(client);
    }
    
    @GET
    @Path("dinner")
    public Multi<MenuItem> dinner() {
        return Menu.dinner.findAll(client);
    }

    @GET
    @Path("{menu}/{day}")
    public Multi<MenuItem> getSingle(@PathParam("menu") String menu, @PathParam("day") String day) {
        return Menu.valueOf(menu).findByDay(client, day);
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