package com.GopaShopping.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.GopaShopping.Entities.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    public List<Cart> findByUserIdOrderByIdDesc(Long userId);

    @Modifying
    @Transactional
    public void deleteByUserId(Long id);
}
