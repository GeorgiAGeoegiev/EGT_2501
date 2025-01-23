package com.example.demo.repository;

import com.example.demo.dao.StatisticDataEntryDao;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static com.example.demo.configuration.SpringCacheConfiguration.REQUEST_IDS_CACHE_NAME;


@Repository
public interface StatisticRepository extends JpaRepository<StatisticDataEntryDao, Long> {

    //there is a DB index by default on the primary key so this will be fast lookup
    @CachePut(value = REQUEST_IDS_CACHE_NAME)
    boolean existsByRequestId(String requestId);
}
