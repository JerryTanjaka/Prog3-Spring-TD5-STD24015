package hei.td5ingredient.controller;

import hei.td5ingredient.entity.Dish;
import hei.td5ingredient.entity.DishIngredient;
import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.repository.DishRepository;
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
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody List<DishIngredient> incomingItems) {

        if (dishService.getDishById(id) == null) {
            return ResponseEntity.status(404).body("Dish.id=" + id + " is not found");
        }

        Dish updatedDish = dishService.updateDishIngredients(id, incomingItems);

        return ResponseEntity.ok(updatedDish);
    }
}