package com.mycompany.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
@Table(name="countries")
public class Country {
    @EqualsAndHashCode.Include
    @Id
    private Integer id;

    @Column(name = "abbreviation")
    private String abbreviation;

    @Column(name = "name")
    private String name;
}
