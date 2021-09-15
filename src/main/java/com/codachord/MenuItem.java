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

    public int day;

    public String name;

    public boolean active = true;

    public MenuItem() {
    }

    public MenuItem(String name) {
        this.name = name;
    }

    public MenuItem(int id, String name) {
        this.day = id;
        this.name = name;
    }

    static MenuItem from(Row row) {
        return new MenuItem(row.getInteger("day"), row.getString("name"));
    }

    public Uni<Long> save(MySQLPool client) {
        return client.preparedQuery("INSERT INTO dinner_menu (name, active) VALUES (?, ?) ON DUPLICATE KEY UPDATE name=?, active=?").execute(Tuple.of(name, active, name, active))
                .onItem().transform(pgRowSet -> pgRowSet.property(MySQLClient.LAST_INSERTED_ID));
    }

    public static Multi<MenuItem> findAll(MySQLPool client) {
        return client.query("SELECT day, name FROM dinner_menu").execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(MenuItem::from);
    }

    public static Uni<MenuItem> findById(MySQLPool client, int id) {
        return client.preparedQuery("SELECT day, name FROM dinner_menu WHERE day = ?").execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }


    public static Uni<Boolean> delete(MySQLPool client, Long id) {
        return client.preparedQuery("DELETE FROM dinner_menu WHERE day = ?").execute(Tuple.of(id))
                .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }
}