package com.practice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "otp_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int otp;

    private String email;

    public Otp(int otp, String email) {
        this.otp = otp;
        this.email = email;
    }
}
