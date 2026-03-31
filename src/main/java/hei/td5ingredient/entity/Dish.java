package hei.td5ingredient.entity;

import hei.td5ingredient.entity.enums.DishTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dish {
    private Integer id;
    private String name;
    private Double sellingPrice;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients;
}