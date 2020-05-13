package org.elsys.fileshare.db;

public class Child {
    public String name;
    public int id;
    public int content_id;
    public String token;

    public Child(NodeEntity entity) {
        this.name = entity.name;
        this.id = entity.id;
        this.content_id = entity.content != null ? entity.content.id : -1;
        this.token = entity.link != null ? entity.link.token : null;
    }
}
