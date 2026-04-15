package hei.td5ingredient.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import hei.td5ingredient.entity.enums.UnitEnum;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockValue {

    @JsonProperty("quantity")
    private Double amount;

    private UnitEnum unit;
}