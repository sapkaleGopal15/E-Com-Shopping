package com.GopaShopping.Repositories;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GopaShopping.Entities.Orders;

@Repository
public interface OrdersRepoitory extends JpaRepository<Orders, Long> {

    public List<Orders> findByUserId(Long userId);
}
