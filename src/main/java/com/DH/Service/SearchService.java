package com.DH.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.DH.Entity.Search;
import com.DH.Entity.User;
import com.DH.Repository.SearchRepository;

@Service
public class SearchService {

@Autowired
protected SearchRepository searchRepository;

public void save(Search search) {
    searchRepository.save(search);
} 

public List<Search> findById(int id)
{
	return searchRepository.findById(id);
}
}