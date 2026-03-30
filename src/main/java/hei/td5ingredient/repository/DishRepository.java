package hei.td5ingredient.repository;

import hei.td5ingredient.Enum.CategoryEnum;
import hei.td5ingredient.entity.Dish;
import hei.td5ingredient.entity.Ingredient;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishRepository {
    private final DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Dish> findAll() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT id, name, selling_price FROM dish";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Dish d = new Dish();
                d.setId(rs.getString("id"));
                d.setName(rs.getString("name"));
                d.setUnitPrice(rs.getDouble("selling_price"));
                d.setIngredients(findIngredientsByDishId(d.getId()));
                dishes.add(d);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return dishes;
    }

    public Dish findById(String id) {
        String sql = "SELECT id, name, selling_price FROM dish WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Dish d = new Dish();
                d.setId(rs.getString("id"));
                d.setName(rs.getString("name"));
                d.setUnitPrice(rs.getDouble("selling_price"));
                d.setIngredients(findIngredientsByDishId(d.getId()));
                return d;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public void updateDishIngredients(int dishId, List<Ingredient> ingredients) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dish_ingredient WHERE id_dish = ?")) {
                ps.setInt(1, dishId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO dish_ingredient (id_dish, id_ingredient) VALUES (?, ?)")) {
                for (Ingredient ing : ingredients) {
                    ps.setInt(1, dishId);
                    ps.setInt(2, ing.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<Ingredient> findIngredientsByDishId(String dishId) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT i.id, i.name, i.category, i.price FROM ingredient i " +
                "JOIN dish_ingredient di ON i.id = di.id_ingredient WHERE di.id_dish = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(dishId));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ingredients.add(new Ingredient(rs.getInt("id"), rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category")), rs.getDouble("price")));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return ingredients;
    }
}