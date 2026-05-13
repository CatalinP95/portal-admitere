package com.campus.dormitory.api;

import com.campus.dormitory.dto.PriceDto;
import com.campus.dormitory.model.Price;
import com.campus.dormitory.service.PriceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dormitory/prices")
@Tag(name = "Prices", description = "Tarife camin")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    public List<Price> getAll() { return priceService.findAll(); }

    @GetMapping("/{id}")
    public Price getOne(@PathVariable Integer id) { return priceService.findById(id); }

    @GetMapping("/by-block/{blockId}")
    public List<Price> getByBlock(@PathVariable Integer blockId) {
        return priceService.findByBlockId(blockId);
    }

    @PostMapping
    public ResponseEntity<Price> create(@Valid @RequestBody PriceDto dto,
                                        @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.status(201).body(
                priceService.create(dto.getName(), dto.getPrice(), dto.getBlockId(), userId));
    }

    @PutMapping("/{id}")
    public Price update(@PathVariable Integer id, @Valid @RequestBody PriceDto dto,
                        @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return priceService.update(id, dto.getName(), dto.getPrice(), dto.getBlockId(), userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        priceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
