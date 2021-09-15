package com.codachord;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLClient;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.LocalDate;

import static com.codachord.Fruit.from;

public class MenuCalendar {

    public LocalDate start;
    public MenuCalendar() {
        this.start = LocalDate.now();
    }

    public MenuCalendar(LocalDate start) {
        this.start = start;
    }

    static MenuCalendar from(Row row) {
        return new MenuCalendar(row.getLocalDate("start"));
    }

    public Uni<Long> save(MySQLPool client) {
        return client.preparedQuery("INSERT INTO menu_calendar (start) VALUES (?) ON DUPLICATE KEY UPDATE start=?").execute(Tuple.of(start, start))
                .onItem().transform(pgRowSet -> pgRowSet.property(MySQLClient.LAST_INSERTED_ID));
    }

    public static Uni<MenuCalendar> findById(MySQLPool client) {
        return client.preparedQuery("SELECT start FROM menu_calendar").execute()
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : new MenuCalendar());
    }
}