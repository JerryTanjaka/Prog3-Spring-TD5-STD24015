package hei.td5ingredient.controller;

import hei.td5ingredient.entity.Dish;
import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping()
    public ResponseEntity<List<Dish>> getDishes() {
        return ResponseEntity.ok(dishService.getDishes());
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable Integer id,
            @RequestBody(required = false) List<Ingredient> ingredients) {

        if (ingredients == null) {
            return ResponseEntity.status(400)
                    .body("Request body is required and must contain a list of ingredients.");
        }

        Dish dish = dishService.getDishById(id);
        if (dish == null) {
            return ResponseEntity.status(404)
                    .body("Dish.id=" + id + " is not found");
        }

        Dish updated = dishService.updateDishIngredients(id, ingredients);
        return ResponseEntity.ok(updated);
    }
}