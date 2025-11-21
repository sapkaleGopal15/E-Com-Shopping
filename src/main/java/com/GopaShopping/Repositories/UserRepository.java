package com.GopaShopping.Repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GopaShopping.Entities.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);
    public List<User> findByRole(String role);
}
