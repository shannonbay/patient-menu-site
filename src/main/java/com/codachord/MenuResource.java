package com.codachord;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.Session;
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

    @Inject
    Session session;

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
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Multi<Response> dinner(List<Dinner> menuitems) {
        return Multi.createFrom().iterable(menuitems).onItem().transform(lunch -> { session.saveOrUpdate(lunch); return Response.accepted().build(); } );
    }

    @POST
    @Path("lunch")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Multi<Response> lunch(List<Lunch> menuitems) {
        return Multi.createFrom().iterable(menuitems).onItem().transform(lunch -> { session.saveOrUpdate(lunch); return Response.accepted().build(); } );
    }

    @GET
    @Transactional
    @Path("lunch/{day}")
    public List<Lunch> getLunch(@PathParam("day") String day) {
        List<Lunch> dinners = session.createQuery("FROM " + Lunch.class.getSimpleName() + " where day = '" + day + "'").getResultList();
        return dinners;
    }

    @GET
    @Transactional
    @Path("dinner/{day}")
    public List<Dinner> getDinner(@PathParam("day") String day) {
        List<Dinner> dinners = session.createQuery("FROM " + Dinner.class.getSimpleName() + " where day = '" + day + "'").getResultList();
        return dinners;
    }

    @GET
    @Path("calendar_start")
    public Uni<MenuCalendar> getCalendarStart() {
        return MenuCalendar.findById(client);
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