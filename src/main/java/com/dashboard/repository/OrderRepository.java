package com.dashboard.repository;

import com.dashboard.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);
    List<Order> findByCustomerName(String customerName);
    
    @Query("SELECT SUM(o.amount) FROM Order o WHERE o.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();
    
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderCountByStatus();
    
    @Query("SELECT DATE(o.orderDate), SUM(o.amount) FROM Order o WHERE o.status = 'COMPLETED' GROUP BY DATE(o.orderDate) ORDER BY DATE(o.orderDate) DESC")
    List<Object[]> getDailyRevenue();
}