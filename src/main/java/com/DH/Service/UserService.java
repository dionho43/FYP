package com.DH.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.DH.Entity.User;
import com.DH.Repository.UserRepository;



@Service
public class UserService {

@Autowired
protected UserRepository userRepository;

public void save(User user) {
    userRepository.save(user);
} 
public List<User> findByUsername(String username)
{
	return userRepository.findByUsername(username);
}
}