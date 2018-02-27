package com.DH.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.DH.Entity.Purchase;
import com.DH.Repository.PurchaseRepository;

@Service
public class PurchaseService {

@Autowired
protected PurchaseRepository purchaseRepository;

public void save(Purchase purch) {
    purchaseRepository.save(purch);
} 
}