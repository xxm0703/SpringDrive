package org.elsys.fileshare.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue
    public int id;

    @NotNull
    @Column(unique = true)
    public String username;

    @NotNull
    private String password;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "root_id", nullable = false)
    @JsonIgnore
    private NodeEntity root;
    @JsonIgnore
    public String uuid;

    public UserEntity() {
    }

    public UserEntity(@NotNull String username, @NotNull String password, NodeEntity root) {
        this.username = username;
        setPassword(password);
        this.root = root;
    }

    public NodeEntity getRoot() {
        return root;
    }

    private void setPassword(String password) {
        this.password = String.valueOf(password.hashCode());
    }

    public boolean correctPass(String pass) {
        return Integer.parseInt(this.password) == pass.hashCode();
    }
}
