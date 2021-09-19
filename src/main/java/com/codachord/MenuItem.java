package com.codachord;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLClient;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import static com.codachord.Fruit.from;

public class MenuItem {

    public String day;

    public String name;

    public MenuItem() {
    }

    public MenuItem(String name) {
        this.name = name;
    }

    public MenuItem(String day, String name) {
        this.day = day;
        this.name = name;
    }

    static MenuItem from(Row row) {
        return new MenuItem(row.getString("day"), row.getString("name"));
    }

    public Uni<Long> save(MySQLPool client) {
        return client.preparedQuery("INSERT INTO dinner_menu (day, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE day=?, name=?").execute(Tuple.of(day, name, day, name))
                .onItem().transform(pgRowSet -> pgRowSet.property(MySQLClient.LAST_INSERTED_ID));
    }

    public static Multi<MenuItem> findAll(MySQLPool client) {
        return client.query("SELECT day, name FROM dinner_menu ORDER BY day").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(MenuItem::from);
    }

    public static Uni<MenuItem> findById(MySQLPool client, String day) {
        return client.preparedQuery("SELECT day, name FROM dinner_menu WHERE day = ?").execute(Tuple.of(day))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }


    public static Uni<Boolean> delete(MySQLPool client, String day) {
        return client.preparedQuery("DELETE FROM dinner_menu WHERE day = ?").execute(Tuple.of(day))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }
}