package com.dashboard.config;

import com.dashboard.entity.User;
import com.dashboard.entity.Order;
import com.dashboard.entity.Product;
import com.dashboard.repository.UserRepository;
import com.dashboard.repository.OrderRepository;
import com.dashboard.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        // 샘플 사용자 데이터
        userRepository.save(new User("김철수", "kim@company.com", "개발팀", "ACTIVE"));
        userRepository.save(new User("이영희", "lee@company.com", "마케팅팀", "ACTIVE"));
        userRepository.save(new User("박민수", "park@company.com", "영업팀", "ACTIVE"));
        userRepository.save(new User("최지영", "choi@company.com", "디자인팀", "INACTIVE"));
        userRepository.save(new User("정현우", "jung@company.com", "개발팀", "ACTIVE"));
        userRepository.save(new User("홍수진", "hong@company.com", "인사팀", "ACTIVE"));
        userRepository.save(new User("장혜진", "jang@company.com", "마케팅팀", "ACTIVE"));
        userRepository.save(new User("오진택", "oh@company.com", "영업팀", "INACTIVE"));
        
        // 샘플 제품 데이터
        productRepository.save(new Product("노트북", "전자제품", new BigDecimal("1200000"), 15, "ACTIVE", "고성능 비즈니스 노트북"));
        productRepository.save(new Product("마우스", "전자제품", new BigDecimal("50000"), 5, "ACTIVE", "무선 광학 마우스"));
        productRepository.save(new Product("키보드", "전자제품", new BigDecimal("80000"), 8, "ACTIVE", "기계식 키보드"));
        productRepository.save(new Product("모니터", "전자제품", new BigDecimal("300000"), 12, "ACTIVE", "24인치 FHD 모니터"));
        productRepository.save(new Product("책상", "가구", new BigDecimal("200000"), 25, "ACTIVE", "사무용 책상"));
        productRepository.save(new Product("의자", "가구", new BigDecimal("150000"), 3, "ACTIVE", "인체공학적 사무 의자"));
        productRepository.save(new Product("램프", "가구", new BigDecimal("45000"), 20, "ACTIVE", "LED 책상 램프"));
        productRepository.save(new Product("태블릿", "전자제품", new BigDecimal("500000"), 1, "DISCONTINUED", "10인치 태블릿"));
        
        // 샘플 주문 데이터
        orderRepository.save(new Order("ORD-001", "김철수", "노트북", 2, new BigDecimal("2400000"), "COMPLETED"));
        orderRepository.save(new Order("ORD-002", "이영희", "마우스", 5, new BigDecimal("250000"), "COMPLETED"));
        orderRepository.save(new Order("ORD-003", "박민수", "키보드", 3, new BigDecimal("240000"), "PENDING"));
        orderRepository.save(new Order("ORD-004", "최지영", "모니터", 1, new BigDecimal("300000"), "COMPLETED"));
        orderRepository.save(new Order("ORD-005", "정현우", "책상", 1, new BigDecimal("200000"), "SHIPPED"));
        orderRepository.save(new Order("ORD-006", "홍수진", "의자", 2, new BigDecimal("300000"), "COMPLETED"));
        orderRepository.save(new Order("ORD-007", "장혜진", "램프", 4, new BigDecimal("180000"), "PENDING"));
        orderRepository.save(new Order("ORD-008", "오진택", "태블릿", 1, new BigDecimal("500000"), "CANCELLED"));
        orderRepository.save(new Order("ORD-009", "김철수", "모니터", 2, new BigDecimal("600000"), "COMPLETED"));
        orderRepository.save(new Order("ORD-010", "이영희", "노트북", 1, new BigDecimal("1200000"), "SHIPPED"));
    }
}