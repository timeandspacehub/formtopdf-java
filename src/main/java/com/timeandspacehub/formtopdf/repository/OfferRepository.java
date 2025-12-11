package com.timeandspacehub.formtopdf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.timeandspacehub.formtopdf.entity.Offer;
import com.timeandspacehub.formtopdf.entity.User;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer>{
	
//	List<Offer> findByCreatedByUsername(String username);
	
	@Query("FROM Offer WHERE createdBy = :userid")
	List<Offer> getOffers(@Param("userid") User firstName);

}
