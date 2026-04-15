package com.ZorvynFinanceApp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ZorvynFinanceApp.backend.models.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
        User findByUsername(String username);
}
