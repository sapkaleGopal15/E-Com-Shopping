package com.GopaShopping.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GopaShopping.Entities.Products;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long>{

    //@Query("SELECT p FROM Products p WHERE p.category = :category")
    //public List<Products> productsFindAllByCategory(@Param("category") String category);
    public List<Products> findByCategory(String category);
}
