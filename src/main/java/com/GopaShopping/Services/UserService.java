package com.GopaShopping.Services;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GopaShopping.Entities.User;
import com.GopaShopping.Repositories.UserRepository;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User getUserByEmail(String email){

        Optional<User> optional = userRepository.findByEmail(email);
        return optional.orElse(null);
    }

}
