package hei.td5ingredient.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import hei.td5ingredient.entity.Enum.UnitEnum;
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

    @JsonProperty("quantity_required")
    private Double quantity;

    private UnitEnum unit;
}