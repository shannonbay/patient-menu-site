package com.codachord;

import com.codachord.MenuItem;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;

@Entity
public class Dinner {

    @EmbeddedId
    @Embedded
    @JsonUnwrapped
    public MenuItem item;

}