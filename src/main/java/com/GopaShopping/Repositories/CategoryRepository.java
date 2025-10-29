package com.GopaShopping.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GopaShopping.Entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    //public Boolean existByName(String name);
    public Category findByName(String name);
}
