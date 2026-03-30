package hei.td5ingredient.repository;

import hei.td5ingredient.Enum.CategoryEnum;
import hei.td5ingredient.Enum.MovementTypeEnum;
import hei.td5ingredient.entity.Ingredient;
import hei.td5ingredient.entity.StockMovement;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IngredientRepository {
    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        Ingredient ingredient = null;
        String sql = "SELECT id FROM ingredient";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }

    public Ingredient findById(int id) {
        String sql = "SELECT id FROM ingredient WHERE id = ?";
        Ingredient ingredient =null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ingredient =new Ingredient();
                    ingredient.setId( rs.getInt("id"));
                    ingredient.setName(  rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                }
                return ingredient;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}