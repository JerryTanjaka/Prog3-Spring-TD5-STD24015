package hei.td5ingredient.controller;

import hei.td5ingredient.entity.StockMovement;
import hei.td5ingredient.service.StockMovementService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController

@RequestMapping("/ingredients")
public class StockMovementController {

    private final StockMovementService service;

    public StockMovementController(StockMovementService service) {
        this.service = service;
    }

    @GetMapping("/{id}/stockMovements")
    public List<StockMovement> getMovements(
            @PathVariable int id,
            @RequestParam Instant from,
            @RequestParam Instant to) {
        return service.getMovementsInRange(id, from, to);
    }

    @PostMapping("/{id}/stockMovements")
    public List<StockMovement> postMovements(
            @PathVariable int id,
            @RequestBody List<StockMovement> movements) {
        return service.addMovements(id, movements);
    }
}