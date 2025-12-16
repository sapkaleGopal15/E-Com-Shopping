package com.GopaShopping.Repositories;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GopaShopping.Entities.Orders;

@Repository
public interface OrdersRepoitory extends JpaRepository<Orders, Long> {
    public List<Orders> findByUserIdOrderByIdDesc(Long userId);
    public Page<Orders> findAllByOrderByIdDesc(Pageable pageable);
    public List<Orders> findByStatusOrderByIdDesc(String status);
    public List<Orders> findByIdOrderByIdDesc(Long id);
    public List<Orders> findByOrderDate(LocalDate date);
}
