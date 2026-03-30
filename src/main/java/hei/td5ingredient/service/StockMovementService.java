package hei.td5ingredient.service;



import hei.td5ingredient.Enum.UnitEnum;
import hei.td5ingredient.entity.StockValue;
import hei.td5ingredient.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    public StockValue getStockValueAt(Integer ingredientId, Instant at, UnitEnum unit) {
        return stockMovementRepository.getStockValueAt(ingredientId, at, unit);
    }
}