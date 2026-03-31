package hei.td5ingredient.service;



import hei.td5ingredient.entity.enums.UnitEnum;
import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.entity.StockMovement;
import hei.td5ingredient.entity.StockValue;
import hei.td5ingredient.exception.NotFoundException;
import hei.td5ingredient.repository.IngredientRepository;
import hei.td5ingredient.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final StockMovementService stockMovementService;
    private final StockMovementRepository stockMovementRepository;

    public IngredientService(IngredientRepository ingredientRepository, StockMovementService stockMovementService,StockMovementRepository stockMovementRepository) {
        this.ingredientRepository = ingredientRepository;
        this.stockMovementService = stockMovementService;
        this.stockMovementRepository= stockMovementRepository;
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

    //stock movements
    public List<StockMovement> getMovements(int id, Instant from, Instant to) {

        this.getIngredientById(id);

        return stockMovementRepository.findByIngredientInRange(id, from, to);
    }

    public List<StockMovement> addMovements(int id, List<StockMovement> movements) {
        this.getIngredientById(id);

        return stockMovementRepository.saveAll(id, movements);
    }
}