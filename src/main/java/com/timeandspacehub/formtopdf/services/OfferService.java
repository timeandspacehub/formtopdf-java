package com.timeandspacehub.formtopdf.services;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.timeandspacehub.formtopdf.entity.Offer;
import com.timeandspacehub.formtopdf.repository.OfferRepository;


@Service
public class OfferService {
    
    private OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository){
        this.offerRepository = offerRepository;
    }

    public List<Offer> getAllOffer(){
        return offerRepository.findAll();
    }

    public Offer createOffer(Offer offer){
        offer.setCreatedDt(LocalDateTime.now()); 
        return offerRepository.save(offer);
    }
}
