package org.elsys.fileshare.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "nodes")
public class NodeEntity {
    @Id
    @GeneratedValue
    public Integer id;
    @NotNull
    public String name;
    public Integer gid;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    public NodeEntity parent;

    @OneToMany(mappedBy = "parent")
    public List<NodeEntity> children;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content_id")
    public ContentEntity content;

    @JsonIgnore
    @OneToOne(mappedBy = "root")
    public UserEntity owner;

    @NotNull
    public Date createdAt;

    @PrePersist
    protected void onCreate() {
//        if (parent == null)
//            parent = this;
        createdAt = new Date();
    }

    protected NodeEntity() {
    }

    public NodeEntity(String name, NodeEntity parent, ContentEntity content) {
        this.name = name;
        this.parent = parent;
        this.content = content;
    }

    public NodeEntity(String name, NodeEntity parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String toString() {
        List<NodeEntity> tempChildren = children.stream()
                .filter(nodeEntity -> nodeEntity.id.equals(this.id))
                .collect(Collectors.toList());
        return "NodeEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gid=" + gid +
                ", children=" + tempChildren +
                ", content=" + content +
                ", createdAt=" + createdAt +
                '}';
    }

}
