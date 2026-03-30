package hei.td5ingredient.entity;


import hei.td5ingredient.Enum.UnitEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DishIngredient {
    private Ingredient ingredient;
    private Double quantity;
    private UnitEnum unit;
}