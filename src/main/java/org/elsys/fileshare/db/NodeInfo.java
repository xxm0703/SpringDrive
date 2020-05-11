package org.elsys.fileshare.db;

import java.sql.Blob;
import java.util.List;
import java.util.stream.Collectors;

public class NodeInfo {
    public final int id;
    public final String name;
    public final byte[] content;
    public final List<Child> children;
    public final int parent;

    public NodeInfo(NodeEntity nodeEntity) {
        this.id = nodeEntity.id;
        this.name = nodeEntity.name;
        this.children = nodeEntity.children.stream()
                .map(Child::new)
                .collect(Collectors.toList());

        this.parent = nodeEntity.parent == null ? nodeEntity.id : nodeEntity.parent.id;
        this.content = nodeEntity.content != null ? nodeEntity.content.text : null;
    }

    public NodeInfo(UserEntity userEntity) {
        this(userEntity.getRoot());
    }
}
