package com.campus.dormitory.api;

import com.campus.dormitory.dto.RoomDto;
import com.campus.dormitory.model.Room;
import com.campus.dormitory.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dormitory/rooms")
@Tag(name = "Rooms", description = "Camere")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAll() { return roomService.findAll(); }

    @GetMapping("/{id}")
    public Room getOne(@PathVariable Integer id) { return roomService.findById(id); }

    @GetMapping("/by-floor/{floorId}")
    public List<Room> getByFloor(@PathVariable Integer floorId) {
        return roomService.findByFloorId(floorId);
    }

    @PostMapping
    public ResponseEntity<Room> create(@Valid @RequestBody RoomDto dto,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.status(201).body(
                roomService.create(dto.getName(), dto.getType(), dto.getNumberSeats(), dto.getFloorId(), userId));
    }

    @PutMapping("/{id}")
    public Room update(@PathVariable Integer id, @Valid @RequestBody RoomDto dto,
                       @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return roomService.update(id, dto.getName(), dto.getType(), dto.getNumberSeats(), dto.getFloorId(), userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
