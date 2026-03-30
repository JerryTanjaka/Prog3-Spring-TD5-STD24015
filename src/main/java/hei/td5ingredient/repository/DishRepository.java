package hei.td5ingredient.repository;

import hei.td5ingredient.entity.Enum.CategoryEnum;
import hei.td5ingredient.entity.Enum.DishTypeEnum;
import hei.td5ingredient.entity.Enum.UnitEnum;
import hei.td5ingredient.entity.Dish;
import hei.td5ingredient.entity.DishIngredient;
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

    public List<Dish> getDishes() {
        String sql = "SELECT id, name, selling_price, dish_type FROM dish ORDER BY id ASC";
        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setSellingPrice(rs.getObject("selling_price") == null ? null : rs.getDouble("selling_price"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));

                dish.setDishIngredients(getIngredientsByDishId(dish.getId()));
                dishes.add(dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des plats", e);
        }
        return dishes;
    }

    public Dish getDishById(Integer id) {
        String sql = "SELECT id, name, selling_price, dish_type FROM dish WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Dish dish = new Dish();
                    dish.setId(rs.getInt("id"));
                    dish.setName(rs.getString("name"));
                    dish.setSellingPrice(rs.getObject("selling_price") == null ? null : rs.getDouble("selling_price"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                    dish.setDishIngredients(getIngredientsByDishId(id));
                    return dish;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du plat id=" + id, e);
        }
        return null;
    }

    private List<DishIngredient> getIngredientsByDishId(Integer dishId) {
        String sql = """
                SELECT i.id, i.name, i.price, i.category, di.required_quantity, di.unit
                FROM ingredient i
                JOIN dish_ingredient di ON di.id_ingredient = i.id
                WHERE di.id_dish = ?
                """;
        List<DishIngredient> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ingredient.setPrice(rs.getDouble("price"));

                    DishIngredient dishIngredient = new DishIngredient();
                    dishIngredient.setIngredient(ingredient);
                    dishIngredient.setQuantity(rs.getObject("required_quantity") == null ? null : rs.getDouble("required_quantity"));
                    dishIngredient.setUnit(UnitEnum.valueOf(rs.getString("unit")));

                    list.add(dishIngredient);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void updateDishIngredients(int dishId, List<DishIngredient> items) {
        String deleteSql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        String insertSql = "INSERT INTO dish_ingredient (id_dish, id_ingredient, required_quantity, unit) VALUES (?, ?, ?, ?::unit)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                psDel.setInt(1, dishId);
                psDel.executeUpdate();
            }

            try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                for (DishIngredient item : items) {
                    if (item.getQuantity() == null || item.getUnit() == null) {
                        continue;
                    }

                    psIns.setInt(1, dishId);
                    psIns.setInt(2, item.getIngredient().getId());
                    psIns.setDouble(3, item.getQuantity());
                    psIns.setString(4, item.getUnit().name());
                    psIns.addBatch();
                }
                psIns.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL lors du PUT /dishes : " + e.getMessage(), e);
        }
    }
}