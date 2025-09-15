package com.example.aggregator.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/donations")
public class DonationAggregatorController {
    private BigDecimal total = BigDecimal.ZERO;
    
    @PostMapping
    public synchronized ResponseEntity<Void> processDonation(@RequestBody Donation donation) {
        total = total.add(donation.getAmount());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/total")
    public ResponseEntity<Response> getTotal() {
        return ResponseEntity.ok(new Response(total));
    }
    
    private record Response(BigDecimal total) {}
    
    
    public static class Donation {
        private String donor;
        private BigDecimal amount;
        
        public Donation() {}

        public Donation(String donor, BigDecimal amount) {
            this.donor = donor;
            this.amount = amount;
        }

        public String getDonor() {
            return donor;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }
}
