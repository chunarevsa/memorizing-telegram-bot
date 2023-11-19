package org.memorizing.repository;

import org.memorizing.entity.CardStockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardStockHistoryRepository extends JpaRepository<CardStockHistory, Long> {

    List<CardStockHistory> findAllByCardStockId(Integer cardStockId);
}
