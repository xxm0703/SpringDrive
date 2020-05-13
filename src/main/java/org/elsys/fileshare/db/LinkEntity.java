package org.elsys.fileshare.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
public class LinkEntity {
    @Id
    @GeneratedValue
    public int id;

    @NotNull
    public String token;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "node_id")
    public NodeEntity node;

    public LinkEntity(NodeEntity node) {
        this.node = node;
        this.token = UUID.randomUUID().toString();
    }

    public LinkEntity() {
    }
}
