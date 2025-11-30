package com.timeandspacehub.formtopdf.services;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails.Address;
import org.springframework.stereotype.Service;

import com.timeandspacehub.formtopdf.entity.Buyer;
import com.timeandspacehub.formtopdf.repository.BuyerRepository;
import com.timeandspacehub.formtopdf.request.CreateBuyerRequest;

@Service
public class BuyerService {

    private final BuyerRepository buyerRepository;


    public BuyerService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    public List<Buyer> getAllBuyers(){
        return buyerRepository.findAll();
    }

    public Buyer createBuyer(CreateBuyerRequest createBuyerRequest) {
		//1.  Convert payload to entity class
		Buyer buyer = new Buyer(createBuyerRequest);

        Buyer savedBuyerInDB = buyerRepository.save(buyer);

        return savedBuyerInDB;
	}

}
