package hei.td5ingredient.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import hei.td5ingredient.entity.enums.UnitEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockValue {

    @JsonProperty("quantity")
    private Double amount;

    private UnitEnum unit;
}