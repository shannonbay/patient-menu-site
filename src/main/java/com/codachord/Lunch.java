package com.codachord;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Lunch {

    @EmbeddedId
    @Embedded
    @JsonUnwrapped
    public MenuItem item;

}