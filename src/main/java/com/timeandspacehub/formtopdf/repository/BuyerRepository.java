package com.timeandspacehub.formtopdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.timeandspacehub.formtopdf.entity.Buyer;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Integer>{
    
}