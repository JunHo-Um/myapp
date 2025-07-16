package com.dashboard.repository;

import com.dashboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByStatus(String status);
    List<User> findByDepartment(String department);
    
    @Query("SELECT u.department, COUNT(u) FROM User u GROUP BY u.department")
    List<Object[]> getUserCountByDepartment();
    
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> getUserCountByStatus();
}