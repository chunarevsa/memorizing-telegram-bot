package com.memorizing.repository;

import com.memorizing.entity.CardStockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardStockHistoryRepository extends JpaRepository<CardStockHistory, Long> {

    List<CardStockHistory> findAllByCardStockId(Integer cardStockId);
}
