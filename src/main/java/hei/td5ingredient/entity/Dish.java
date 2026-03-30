package hei.td5ingredient.entity;


import hei.td5ingredient.Enum.DishTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Dish {
    private int id;
    private String name;
    private double unitPrice;
    private List<Ingredient> ingredients;
    private DishTypeEnum dishTypeEnum;
}