package com.GopaShopping.Entities;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private List<Long> productId;

    private List<String> productNames;

    private String name;

    private String contact;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private String paymentMode;

    private Double orderPrice;

    private String status;

    @CreationTimestamp
    private LocalDate orderDate;
}
