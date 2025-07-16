package com.dashboard.controller;

import com.dashboard.entity.User;
import com.dashboard.entity.Order;
import com.dashboard.entity.Product;
import com.dashboard.repository.UserRepository;
import com.dashboard.repository.OrderRepository;
import com.dashboard.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String dashboard(Model model) {
        // 기본 통계 정보
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalRevenue", totalRevenue);
        
        // 최근 사용자들
        List<User> recentUsers = userRepository.findAll().stream()
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .limit(5)
            .toList();
        model.addAttribute("recentUsers", recentUsers);
        
        // 최근 주문들
        List<Order> recentOrders = orderRepository.findAll().stream()
            .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
            .limit(5)
            .toList();
        model.addAttribute("recentOrders", recentOrders);
        
        // 재고 부족 제품들
        List<Product> lowStockProducts = productRepository.findByStockLessThan(10);
        model.addAttribute("lowStockProducts", lowStockProducts);
        
        // 부서별 사용자 수
        List<Object[]> usersByDepartment = userRepository.getUserCountByDepartment();
        model.addAttribute("usersByDepartment", usersByDepartment);
        
        // 상태별 주문 수
        List<Object[]> ordersByStatus = orderRepository.getOrderCountByStatus();
        model.addAttribute("ordersByStatus", ordersByStatus);
        
        return "dashboard";
    }
    
    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }
    
    @GetMapping("/orders")
    public String orders(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "orders";
    }
    
    @GetMapping("/products")
    public String products(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "products";
    }
}