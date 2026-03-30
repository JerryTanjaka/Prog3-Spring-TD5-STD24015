package hei.td5ingredient.controller;


import hei.td5ingredient.entity.Enum.UnitEnum;
import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.entity.StockValue;
import hei.td5ingredient.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;
    public IngredientController(IngredientService ingredientService){
        this.ingredientService= ingredientService;
    }
    @GetMapping()
    public ResponseEntity<List<Ingredient>> getIngredients() {
        return ResponseEntity.ok(ingredientService.getIngredients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable Integer id) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        if (ingredient == null) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }
        return ResponseEntity.ok(ingredient);
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getIngredientStockAt(
            @PathVariable Integer id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {

        if (at == null || unit == null) {
            return ResponseEntity.status(400)
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        Ingredient ingredient = ingredientService.getIngredientById(id);
        if (ingredient == null) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }

        Instant atInstant;
        UnitEnum unitEnum;
        try {
            atInstant = Instant.parse(at);
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body("Invalid value for `at` parameter. Expected ISO-8601 format (e.g. 2024-01-06T12:00:00Z).");
        }
        try {
            unitEnum = UnitEnum.valueOf(unit.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400)
                    .body("Invalid value for `unit` parameter. Expected one of: PCS, KG, L.");
        }

        StockValue stockValue = ingredientService.getStockAt(id, atInstant, unitEnum);
        return ResponseEntity.ok(stockValue)
        ;
    }
}
