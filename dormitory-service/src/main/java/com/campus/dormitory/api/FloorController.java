package com.campus.dormitory.api;

import com.campus.dormitory.dto.FloorDto;
import com.campus.dormitory.model.Floor;
import com.campus.dormitory.service.FloorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dormitory/floors")
@Tag(name = "Floors", description = "Etaje")
public class FloorController {

    private final FloorService floorService;

    public FloorController(FloorService floorService) {
        this.floorService = floorService;
    }

    @GetMapping
    public List<Floor> getAll() { return floorService.findAll(); }

    @GetMapping("/{id}")
    public Floor getOne(@PathVariable Integer id) { return floorService.findById(id); }

    @GetMapping("/by-block/{blockId}")
    @Operation(summary = "Etajele unui camin")
    public List<Floor> getByBlock(@PathVariable Integer blockId) {
        return floorService.findByBlockId(blockId);
    }

    @PostMapping
    public ResponseEntity<Floor> create(@Valid @RequestBody FloorDto dto,
                                        @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.status(201).body(floorService.create(dto.getName(), dto.getBlockId(), userId));
    }

    @PutMapping("/{id}")
    public Floor update(@PathVariable Integer id, @Valid @RequestBody FloorDto dto,
                        @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return floorService.update(id, dto.getName(), dto.getBlockId(), userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        floorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
