package com.test.hibernate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Country {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Continent continent;
}
