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
    private final IngredientRepository ingredientRepository;

    public DishRepository(DataSource dataSource, IngredientRepository ingredientRepository) {
        this.dataSource = dataSource;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Dish> getDishes() {
        String sql = "SELECT id, name, selling_price, dish_type FROM dish ORDER BY id ASC";
        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            Dish dish = new Dish();
            while (rs.next()) {
                dish.setName(rs.getString("name"));
                dish.setId(rs.getInt("id"));
                dish.setSellingPrice(rs.getObject("selling_price") == null ? null : rs.getDouble("selling_price"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setDishIngredients(getIngredientsByDishId(dish.getId()));
                dishes.add(dish);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dishes;
    }

    public Dish getDishById(Integer id) {
        String sql = "SELECT id, name, selling_price, dish_type FROM dish WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                Dish dish = new Dish();
                if (rs.next()) {
                    dish.setId(rs.getInt("id"));
                    dish.setDishIngredients(getIngredientsByDishId(id));
                    dish.setName(rs.getString("name"));
                    dish.setSellingPrice(rs.getObject("selling_price") == null ? null : rs.getDouble("selling_price"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                }
                return dish;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                Ingredient ingredient = new Ingredient();
                DishIngredient dishIngredient = new DishIngredient();
                while (rs.next()) {
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setCategory( CategoryEnum.valueOf(rs.getString("category")));
                    ingredient.setPrice(rs.getDouble("price"));

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

    public Dish updateDishIngredients(Integer dishId, List<Ingredient> requestedIngredients) {

        List<Ingredient> validIngredients = new ArrayList<>();
        for (Ingredient req : requestedIngredients) {
            Ingredient fromDb = ingredientRepository.findIngredientById(req.getId());
            if (fromDb != null) {
                validIngredients.add(fromDb);
            }
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM dish_ingredient WHERE id_dish = ?")) {
                del.setInt(1, dishId);
                del.executeUpdate();
            }

            String insertSql = "INSERT INTO dish_ingredient (id_dish, id_ingredient) VALUES (?, ?)";
            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                for (Ingredient ing : validIngredients) {
                    ins.setInt(1, dishId);
                    ins.setInt(2, ing.getId());
                    ins.addBatch();
                }
                ins.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return getDishById(dishId);
    }
}