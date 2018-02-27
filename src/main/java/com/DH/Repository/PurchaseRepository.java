package com.DH.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.DH.Entity.Purchase;


@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
	
}