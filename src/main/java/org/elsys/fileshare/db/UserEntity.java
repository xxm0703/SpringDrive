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

    @JsonIgnore
    @NotNull
    public String email = null;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "root_id", nullable = false)
    @JsonIgnore
    private NodeEntity root;

    @JsonIgnore
    public String uuid;
    @JsonIgnore
    @NotNull
    public boolean enabled = false;

    public UserEntity() {
    }

    public UserEntity(CredentialsContainer data, NodeEntity root) {
        this.root = root;
        this.username = data.username;
        this.email = data.email;
        setPassword(data.password);
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
