package com.example.demo.repository;

import com.example.demo.dao.RatesDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatesRepository extends JpaRepository<RatesDao, Long> {
}
