package org.elsys.fileshare.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity(name = "content")
public class ContentEntity {
    @Id
    @GeneratedValue
    public Integer id;

    @Lob
    public byte[] text;

    @JsonIgnore
    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL)
    public NodeEntity file;

    public ContentEntity() {
    }

    public ContentEntity(byte[] text) {
        this.text = text;
    }
}
