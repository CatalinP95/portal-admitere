package com.campus.dormitory.api;

import com.campus.dormitory.dto.BedDto;
import com.campus.dormitory.model.Bed;
import com.campus.dormitory.service.BedService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dormitory/beds")
@Tag(name = "Beds", description = "Paturi")
public class BedController {

    private final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    @GetMapping
    public List<Bed> getAll() { return bedService.findAll(); }

    @GetMapping("/{id}")
    public Bed getOne(@PathVariable Integer id) { return bedService.findById(id); }

    @GetMapping("/by-room/{roomId}")
    public List<Bed> getByRoom(@PathVariable Integer roomId) {
        return bedService.findByRoomId(roomId);
    }

    @PostMapping
    public ResponseEntity<Bed> create(@Valid @RequestBody BedDto dto,
                                      @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.status(201).body(bedService.create(dto.getName(), dto.getRoomId(), userId));
    }

    @PutMapping("/{id}")
    public Bed update(@PathVariable Integer id, @Valid @RequestBody BedDto dto,
                      @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return bedService.update(id, dto.getName(), dto.getRoomId(), userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bedService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
