package hei.td5ingredient.repository;



import hei.td5ingredient.Enum.CategoryEnum;
import hei.td5ingredient.Enum.DishTypeEnum;
import hei.td5ingredient.Enum.UnitEnum;
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
            dish.setId(rs.getInt("id"));
            dish.setName(rs.getString("name"));
            dish.setUnitPrice(rs.getObject("selling_price") == null ? null : rs.getDouble("selling_price"));
            dish.setDishTypeEnum(DishTypeEnum.valueOf(rs.getString("dish_type")));
            }
                dishes.add(dish);
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
                dish.setDishTypeEnum(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setUnitPrice(rs.getObject("selling_price") == null ? null : rs.getDouble("selling_price"));
                dish.setIngredients(getIngredientsByDishId(id));
                }
                return dish;
                return null;
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
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            CategoryEnum.valueOf(rs.getString("category")),
                            rs.getDouble("price")
                    );
                    DishIngredient di = new DishIngredient(
                            ingredient,
                            rs.getObject("required_quantity") == null ? null : rs.getDouble("required_quantity"),
                            UnitEnum.valueOf(rs.getString("unit"))
                    );
                    list.add(di);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public Dish updateDishIngredients(Integer dishId, List<Ingredient> requestedIngredients) {
        // Resolve only existing ingredients from DB
        List<Ingredient> validIngredients = new ArrayList<>();
        for (Ingredient req : requestedIngredients) {
            Ingredient fromDb = ingredientRepository.getIngredientById(req.getId());
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