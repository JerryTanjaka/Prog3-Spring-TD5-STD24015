package hei.td5ingredient.entity;

import hei.td5ingredient.entity.Enum.MovementTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockMovement {
    private Integer id;
    private MovementTypeEnum type;
    private Instant creationDatetime;
    private StockValue value;
    private int ingredientId;
}