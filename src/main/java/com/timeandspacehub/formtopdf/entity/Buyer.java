package com.timeandspacehub.formtopdf.entity;

import com.timeandspacehub.formtopdf.request.CreateBuyerRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "buyer")
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "ssn")
    private String ssn;

    public Buyer(CreateBuyerRequest request) {
        this.firstName = request.getFirstName();
        this.middleName = request.getMiddleName();
        this.lastName = request.getLastName();
        this.email = request.getEmail();
        this.phone = request.getPhone();
        this.ssn = request.getSsn();
    }
    
}
