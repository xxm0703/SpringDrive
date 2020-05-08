package org.elsys.fileshare.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "content")
public class ContentEntity {
    @Id
    @GeneratedValue
    public Integer id;

    @NotNull
    public String text;

    @JsonIgnore
    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL)
    public NodeEntity file;
}
