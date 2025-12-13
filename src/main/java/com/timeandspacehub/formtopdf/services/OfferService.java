package com.timeandspacehub.formtopdf.services;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.timeandspacehub.formtopdf.entity.Offer;
import com.timeandspacehub.formtopdf.entity.User;
import com.timeandspacehub.formtopdf.repository.OfferRepository;
import com.timeandspacehub.formtopdf.repository.UserRepository;


@Service
public class OfferService {
    
    private OfferRepository offerRepository;
    private UserRepository userRepository;

    public OfferService(OfferRepository offerRepository, UserRepository userRepository){
        this.offerRepository = offerRepository;
        this.userRepository=userRepository;
    }

    public List<Offer> getAllOffer(){
        return offerRepository.findAll();
    }

    public Offer createOffer(Offer offer){
    	//Get current username from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        //Load the User entity
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        double cash = offer.getDownPayment() != null ? offer.getDownPayment() : 0.0;
        double financed = offer.getLoanAmount() != null ? offer.getLoanAmount() : 0.0;
        offer.setSalesPrice(cash + financed);
        offer.setCreatedDt(LocalDateTime.now()); 
        
        offer.setCreatedBy(user);

        return offerRepository.save(offer);
    }
    
    public List<Offer> getMyOffers() {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (isAdmin) {
            return offerRepository.findAll();
        } else {
            // 🙋 Normal user sees only their own offers
        	return offerRepository.getOffers(user);
//             offerRepository.findByCreatedByUsername(username);
        }
    }

}
