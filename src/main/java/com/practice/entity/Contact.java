package com.practice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "contact_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;

    private String name;
    //    private String secondName;
    private String work;
    private String email;
    private String phone;
    private String image;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JsonIgnore // To mark no serialization on this field
    private User user;
}
