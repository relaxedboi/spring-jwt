package com.myproject.luharcom.service;

import com.myproject.luharcom.models.PrincipalUser;
import com.myproject.luharcom.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUserName(username);
        if(user == null){
            throw new NullPointerException("No user found with this username: "+username+" in the system.");
        }
        PrincipalUser principalUser = new PrincipalUser();
        principalUser.setUsername(user.getUserName());
        principalUser.setEmail(user.getEmail());
        principalUser.setPassword(user.getPassword());
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
        principalUser.setAuthorities(grantedAuthorities);
        return principalUser;
//        return new org.springframework.security.core.userdetails.User(user.getUserName(),
//                user.getPassword(), grantedAuthorities);
    }
}
