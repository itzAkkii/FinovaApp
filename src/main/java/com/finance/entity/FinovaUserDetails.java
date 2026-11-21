package com.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "FIN_USER_DETAILS")
public class FinovaUserDetails {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "MOBILE_NO")
    private String mobileNo;

    @Column(name = "DOB")
    private String dob;

    @Column(name = "MARITAL_STATUS")
    private String maritalStatus;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;
}