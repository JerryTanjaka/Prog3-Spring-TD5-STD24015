package hei.td5ingredient.entity;

import hei.td5ingredient.Enum.CategoryEnum;
import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Ingredient {
    private int id;
    private String name;
    private CategoryEnum category;
    private double price;
}