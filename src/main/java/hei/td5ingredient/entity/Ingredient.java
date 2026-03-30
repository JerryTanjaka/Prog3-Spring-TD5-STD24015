package hei.td5ingredient.entity;

import hei.td5ingredient.entity.Enum.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
}