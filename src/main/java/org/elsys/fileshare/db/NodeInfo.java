package org.elsys.fileshare.db;

import java.util.List;
import java.util.stream.Collectors;

public class NodeInfo {

    public List<Child> children;
    public int parent;
    public String name;

    public NodeInfo(NodeEntity nodeEntity) {
        this.name = nodeEntity.name;
        this.children = nodeEntity.children.stream()
                .map(Child::new)
                .collect(Collectors.toList());

        this.parent = nodeEntity.parent == null ? nodeEntity.id : nodeEntity.parent.id;
    }

    public NodeInfo(UserEntity userEntity) {
        this(userEntity.getRoot());
    }
}
