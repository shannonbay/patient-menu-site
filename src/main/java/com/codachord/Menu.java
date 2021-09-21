package com.codachord;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.mysqlclient.MySQLPool;

public enum Menu {

	lunch, dinner;
	
	public Multi<MenuItem> save(List<MenuItem> items, MySQLPool client) {
    	String s = items.stream().map( i -> "(" + i.day + ", '" + i.name + "')").collect(Collectors.joining(", ", "INSERT INTO " + this + " (day, name) VALUES ", " ON DUPLICATE KEY UPDATE day=VALUES(day), name=VALUES(name);"));
    	
    	log.info(s);
    	
        return client.preparedQuery(s).execute()
        		.onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform((row) -> new MenuItem(this.toString(), row));
    }
	
    public Multi<MenuItem> findAll(MySQLPool client) {
        return client.query("SELECT day, optionId, name FROM " + this + " ORDER BY day").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform((row) -> new MenuItem(this.toString(), row));
    }
    
	public Multi<MenuItem> findByDay(MySQLPool client, String day) {
	    return client.query("SELECT day, optionId, name FROM " + this + " ORDER BY day, optionId").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform((row) -> new MenuItem(this.toString(), row));
	}
    
    private static final Logger log = Logger.getLogger(MenuResource.class);

}
