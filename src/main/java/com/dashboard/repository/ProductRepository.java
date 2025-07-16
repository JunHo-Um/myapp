package com.dashboard.repository;

import com.dashboard.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByStatus(String status);
    List<Product> findByStockLessThan(Integer stock);
    
    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> getProductCountByCategory();
    
    @Query("SELECT p.status, COUNT(p) FROM Product p GROUP BY p.status")
    List<Object[]> getProductCountByStatus();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock < 10")
    Long getLowStockCount();
}