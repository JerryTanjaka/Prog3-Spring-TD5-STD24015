package hei.td5ingredient.controller;


import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.repository.IngredientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class ingredientController {
    private final IngredientRepository repository;

    public ingredientController(IngredientRepository repository){
        this.repository = repository;
    }
    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return repository.findAll();
    }


}
