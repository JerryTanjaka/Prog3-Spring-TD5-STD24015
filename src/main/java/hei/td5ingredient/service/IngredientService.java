package hei.td5ingredient.service;

import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository repository;
    public IngredientService(IngredientRepository repository){
        this.repository=repository;
    }
    public List<Ingredient> getAllIngredients() {
        return repository.findAll();
    }
    public Ingredient getIngredientById(int id){
        return repository.findById(id);
    }

}
