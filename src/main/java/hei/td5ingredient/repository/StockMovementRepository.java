package hei.td5ingredient.repository;
import hei.td5ingredient.entity.enums.MovementTypeEnum;
import hei.td5ingredient.entity.enums.UnitEnum;
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

    public StockValue getStockValueAt(Integer ingredientId, Instant at, UnitEnum unit) {
        String sql = """
                SELECT unit,
                       SUM(
                           CASE
                               WHEN type = 'IN'  THEN  quantity
                               WHEN type = 'OUT' THEN -quantity
                               ELSE 0
                           END
                       ) AS actual_quantity
                FROM stock_movement
                WHERE creation_datetime <= ?
                  AND id_ingredient = ?
                  AND unit = ?::unit
                GROUP BY unit
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.from(at));
            ps.setInt(2, ingredientId);
            ps.setString(3, unit.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new StockValue(
                            rs.getDouble("actual_quantity"),
                            UnitEnum.valueOf(rs.getString("unit"))
                    );
                }

                return new StockValue(0.0, unit);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StockMovement> findByIngredientInRange(int ingredientId, Instant from, Instant to) {
        List<StockMovement> list = new ArrayList<>();
        String sql = "SELECT id, quantity, unit, type, creation_datetime FROM stock_movement " +
                "WHERE id_ingredient = ? AND creation_datetime >= ? AND creation_datetime <= ? " +
                "ORDER BY creation_datetime DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ps.setTimestamp(2, Timestamp.from(from));
            ps.setTimestamp(3, Timestamp.from(to));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StockMovement sm = new StockMovement();
                sm.setId(rs.getInt("id"));
                sm.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                sm.setType(MovementTypeEnum.valueOf(rs.getString("type")));
                sm.setValue(new StockValue(rs.getDouble("quantity"), UnitEnum.valueOf(rs.getString("unit"))));
                list.add(sm);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<StockMovement> saveAll(int ingredientId, List<StockMovement> movements) {
        String sql = "INSERT INTO stock_movement (id_ingredient, quantity, unit, type, creation_datetime) " +
                "VALUES (?, ?, ?::unit, ?::movement_type, ?) RETURNING id, creation_datetime";

        try (Connection conn = dataSource.getConnection()) {
            for (StockMovement sm : movements) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, ingredientId);


                    ps.setDouble(2, sm.getValue().getAmount());

                    ps.setString(3, sm.getValue().getUnit().name());
                    ps.setString(4, sm.getType().name());


                    Instant now = Instant.now();
                    ps.setTimestamp(5, Timestamp.from(now));

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {

                            sm.setId(rs.getInt("id"));
                            sm.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                        }
                    }
                }
            }
            return movements;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL lors de l'insertion du mouvement : " + e.getMessage(), e);
        }
    }
}