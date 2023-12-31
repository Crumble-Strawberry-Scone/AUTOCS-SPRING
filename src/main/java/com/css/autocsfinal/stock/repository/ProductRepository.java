package com.css.autocsfinal.stock.repository;

import com.css.autocsfinal.stock.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findAll(Pageable paging);
    List<Product> findByStatus(String y);
    Page<Product> findByStatus(String y, Pageable paging);

}
