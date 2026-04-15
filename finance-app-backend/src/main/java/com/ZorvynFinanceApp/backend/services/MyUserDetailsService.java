package com.ZorvynFinanceApp.backend.services;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ZorvynFinanceApp.backend.dtos.AuthRequest;
import com.ZorvynFinanceApp.backend.models.RoleType;
import com.ZorvynFinanceApp.backend.models.User;
import com.ZorvynFinanceApp.backend.models.UserPrincipal;
import com.ZorvynFinanceApp.backend.repositories.UserRepo;

@Service
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class MyUserDetailsService implements UserDetailsService {


    UserRepo userRepo;

    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }

        return new UserPrincipal(user);
    }

    public User creatUser(AuthRequest authRequest) {
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        Set<RoleType> roles = new HashSet<>();
        
        roles.add(RoleType.VIEWER);
        user.setRoles(roles);
        return userRepo.save(user);
    }
}
