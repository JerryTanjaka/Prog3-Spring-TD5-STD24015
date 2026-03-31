package hei.td5ingredient.service;



import hei.td5ingredient.entity.Enum.UnitEnum;
import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.entity.StockValue;
import hei.td5ingredient.exception.NotFoundException;
import hei.td5ingredient.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final StockMovementService stockMovementService;

    public IngredientService(IngredientRepository ingredientRepository, StockMovementService stockMovementService) {
        this.ingredientRepository = ingredientRepository;
        this.stockMovementService = stockMovementService;
    }

    public List<Ingredient> getIngredients() {
        return ingredientRepository.findAllIngredients();
    }

    public Ingredient getIngredientById(int id) {
        Ingredient ing = ingredientRepository.findIngredientById(id);
        if (ing == null) {

            throw new NotFoundException("Ingredient.id=" + id + " is not found");
        }
        return ing;
    }
    public StockValue getStockAt(Integer ingredientId, Instant at, UnitEnum unit) {
        return stockMovementService.getStockValueAt(ingredientId, at, unit);
    }
}