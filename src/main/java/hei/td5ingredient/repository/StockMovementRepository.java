package hei.td5ingredient.repository;

import hei.td5ingredient.Enum.MovementTypeEnum;
import hei.td5ingredient.Enum.UnitEnum;
import hei.td5ingredient.entity.StockMovement;
import hei.td5ingredient.entity.StockValue;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StockMovementRepository {
    private final DataSource dataSource;

    public StockMovementRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<StockMovement> findByIngredientIdBefore(int ingredientId, Instant date) {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT id, type, creation_datetime, quantity, unit " +
                "FROM stock_movement WHERE id_ingredient = ? AND creation_datetime <= ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ingredientId);
            pstmt.setTimestamp(2, Timestamp.from(date));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                StockMovement sm = new StockMovement();
                sm.setId(rs.getInt("id"));
                sm.setType(MovementTypeEnum.valueOf(rs.getString("type")));
                sm.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                sm.setValue(new StockValue(rs.getDouble("quantity"), UnitEnum.valueOf(rs.getString("unit"))));
                movements.add(sm);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movements;
    }
}