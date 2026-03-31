package hei.td5ingredient.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import hei.td5ingredient.entity.Enum.UnitEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockValue {
    @JsonProperty("quantity")
    private double amount;

    private UnitEnum unit;
}