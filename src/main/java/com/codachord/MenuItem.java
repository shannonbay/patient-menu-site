package com.codachord;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLClient;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

@Embeddable
public class MenuItem implements Serializable {

    public int day;
    
    public int optionId;

    public String name;

    public MenuItem() {
    }
    
    public MenuItem(Row r) {
        this.day = r.getInteger("day");
        this.optionId = r.getInteger("optionId");
        this.name = r.getString("name");
    }

    public MenuItem(int day, int optionId, String name) {
        this.day = day;
        this.optionId = optionId;
        this.name = name;
    }
}