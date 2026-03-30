package hei.td5ingredient.entity;

import hei.td5ingredient.Enum.UnitEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockValue {
    private double amount;
    private UnitEnum unit;
}