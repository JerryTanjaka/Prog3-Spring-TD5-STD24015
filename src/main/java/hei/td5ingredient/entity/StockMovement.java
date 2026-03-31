package hei.td5ingredient.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import hei.td5ingredient.entity.enums.MovementTypeEnum;
import lombok.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockMovement {
    private Integer id;
    private MovementTypeEnum type;

    @JsonProperty("creation_datetime")
    private Instant creationDatetime;

    private StockValue value;

    @JsonProperty("ingredient_id")
    private Integer ingredientId;
}