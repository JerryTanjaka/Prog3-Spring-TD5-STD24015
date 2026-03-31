package hei.td5ingredient.service;



import hei.td5ingredient.entity.Enum.UnitEnum;
import hei.td5ingredient.entity.StockMovement;
import hei.td5ingredient.entity.StockValue;
import hei.td5ingredient.exception.NotFoundException;
import hei.td5ingredient.repository.IngredientRepository;
import hei.td5ingredient.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final IngredientRepository ingredientRepository;

    public StockMovementService(StockMovementRepository smr, IngredientRepository ir) {
        this.stockMovementRepository = smr;
        this.ingredientRepository = ir;
    }

    public StockValue getStockValueAt(Integer ingredientId, Instant at, UnitEnum unit) {
        return stockMovementRepository.getStockValueAt(ingredientId, at, unit);
    }

    private void checkIngredientExists(int id) {
        if (ingredientRepository.findIngredientById(id) == null) {
            throw new NotFoundException("Ingredient.id={" + id + ") is not found");
        }
    }

    public List<StockMovement> getMovementsInRange(int id, Instant from, Instant to) {
        checkIngredientExists(id);
        return stockMovementRepository.findByIngredientInRange(id, from, to);
    }

    public List<StockMovement> addMovements(int id, List<StockMovement> movements) {
        checkIngredientExists(id);
        return stockMovementRepository.saveAll(id, movements);
    }

}