package com.GopaShopping.Repositories;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GopaShopping.Entities.Products;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long>{

    public Page<Products> findByCategory(String category, Pageable pageable);
    public Page<Products> findByIsActiveTrueOrderByIdDesc(Pageable pageable);
    public List<Products> findByIsActiveTrueAndCategoryOrderByIdDesc(String category);
    public Page<Products> findByIsActiveTrueAndTitle(String title, Pageable pageable);
    public Products findByIsActiveTrueAndId(Long id);
    public Page<Products> findByIsActiveTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);
    public Page<Products> findAllByOrderByIdDesc(Pageable pageable);
    public List<Products> findByPrice(Double price);
    public List<Products> findByIsActive(Boolean isActive);
    

    

    // End user search
    @Query("""
        SELECT p FROM Products p
        WHERE p.isActive = true
        AND (
            :keyword IS NULL OR :keyword = ''
            OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    List<Products> searchActiveProducts(@Param("keyword") String keyword);



    // Admin Search
    @Query("SELECT p FROM Products p WHERE " +
        "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    List<Products> searchProductsByAll(@Param("keyword") String keyword);

    
}
