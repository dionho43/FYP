package com.DH.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.DH.Entity.Search;
import com.DH.Entity.User;


@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {
	
	List<Search> findById(@Param("id") int id);
	
}