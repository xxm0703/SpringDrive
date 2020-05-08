package org.elsys.fileshare.db;

public class Child {
    public String name;
    public int id;
    public int content_id;

    public Child(NodeEntity entity) {
        this.name = entity.name;
        this.id = entity.id;
        this.content_id = entity.content != null ? entity.content.id : -1;
    }
}
