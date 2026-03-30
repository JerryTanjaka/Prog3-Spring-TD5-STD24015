package hei.td5ingredient.service;

import hei.td5ingredient.entity.Dish;
import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> getDishes() {
        return dishRepository.getDishes();
    }

    public Dish getDishById(Integer id) {
        return dishRepository.getDishById(id);
    }

    public Dish updateDishIngredients(Integer dishId, List<Ingredient> ingredients) {
        return dishRepository.updateDishIngredients(dishId, ingredients);
    }
}