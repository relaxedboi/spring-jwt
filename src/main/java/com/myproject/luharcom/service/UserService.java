package com.myproject.luharcom.service;

import com.myproject.luharcom.models.User;
import com.myproject.luharcom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService{

    @Autowired
    UserRepository userRepository;

    public void saveUser(User user){
        userRepository.save(user);
    }

    public void updateUser(User user){
        userRepository.save(user);
    }

    public User getUserByUserName(String userName){
        return userRepository.findByUserName(userName);
    }

    public List<User> getAllUsers(){
        return userRepository.listAllUser();
    }

}
