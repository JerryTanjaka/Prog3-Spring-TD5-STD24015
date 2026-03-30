package hei.td5ingredient.service;

import hei.td5ingredient.entity.Dish;
import hei.td5ingredient.entity.DishIngredient;
import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.repository.DishRepository;
import hei.td5ingredient.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public DishService(DishRepository dishRepository,IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository=ingredientRepository;
    }

    public List<Dish> getDishes() {
        return dishRepository.getDishes();
    }

    public Dish getDishById(Integer id) {
        return dishRepository.getDishById(id);
    }
    public Dish updateDishIngredients(int dishId, List<DishIngredient> incomingItems) {
        List<DishIngredient> validItems = new ArrayList<>();
        if (incomingItems != null) {
            for (DishIngredient item : incomingItems) {
                if (item.getIngredient() != null && item.getIngredient().getId() != null) {
                    int ingId = item.getIngredient().getId();
                    if (ingredientRepository.findIngredientById(ingId) != null) {
                        validItems.add(item);
                    }
                }
            }
        }
        dishRepository.updateDishIngredients(dishId, validItems);
        return this.getDishById(dishId);
    }
}