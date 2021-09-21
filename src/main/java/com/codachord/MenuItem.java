package com.codachord;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLClient;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class MenuItem {

	public String menu;
	
    public String day;
    
    public String optionId;

    public String name;

    public MenuItem() {
    }
    
    public MenuItem(String menu, Row r) {
    	this.menu = menu;
        this.day = r.getString("day");
        this.optionId = r.getString("optionId");
        this.name = r.getString("name");
    }

    public MenuItem(String menu, String day, String optionId, String name) {
    	this.menu = menu;
        this.day = day;
        this.optionId = optionId;
        this.name = name;
    }
    
    public Uni<Long> save(MySQLPool client) {
    	return client.preparedQuery("INSERT INTO " + menu + " (day, optionId, name) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE day=?, optionId=?, name=?")
        		.execute(Tuple.of(day, optionId, name, day, optionId, name))
                .onItem().transform(pgRowSet -> pgRowSet.property(MySQLClient.LAST_INSERTED_ID));
    }
    
    public static class MenuItemQueries extends MenuItem {
    	
    	public MenuItemQueries(String menu) {
			super(menu, "", "", "");
		}
    	
    	public Multi<MenuItem> findAll(MySQLPool client) {
    		return client.query("SELECT day, optionId, name FROM " + this.menu + " ORDER BY day, optionId").execute()
                    .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                    .onItem().transform((row) -> new MenuItem(menu, row));
    	}

        public Multi<MenuItem> save(List<MenuItem> items, MySQLPool client) {
        	String s = items.stream().map( i -> "(" + i.day + ", '" + i.name + "')").collect(Collectors.joining(", ", "INSERT INTO " + this.menu + " (day, name) VALUES ", " ON DUPLICATE KEY UPDATE day=VALUES(day), name=VALUES(name);"));
        	
        	log.info(s);
        	
            return client.preparedQuery(s).execute()
            		.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                    .onItem().transform((row) -> new MenuItem(menu, row));
        }
    }

    public static final MenuItemQueries DINNER_ITEM = new MenuItemQueries("dinner");
    public static final MenuItemQueries LUNCH_ITEM = new MenuItemQueries("lunch");

    private static final Logger log = Logger.getLogger(MenuItem.class);


}