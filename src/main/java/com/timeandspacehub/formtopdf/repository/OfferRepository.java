package com.timeandspacehub.formtopdf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.timeandspacehub.formtopdf.entity.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer>{
	
	List<Offer> findByCreatedByUsername(String username);

    
}
