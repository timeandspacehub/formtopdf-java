package com.timeandspacehub.formtopdf.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "offer")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "lot")
    private long lot;

    @Column(name = "block")
    private String block;

    @Column(name = "subdivision")
    private String subdivision;

    @Column(name = "city")
    private String city;

    @Column(name = "county")
    private String county;

    @Column(name = "full_address")
    private String fulladdress;

    @Column(name = "offered_amount")
    private long offeredAmount;

    @Column(name = "created_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDt;

    @Column(name = "status")
    private String status;
}
