package com.campus.dormitory.api;

import com.campus.dormitory.dto.BlockDto;
import com.campus.dormitory.model.Block;
import com.campus.dormitory.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dormitory/blocks")
@Tag(name = "Blocks", description = "Camine (cladiri)")
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @GetMapping
    @Operation(summary = "Lista tuturor caminelor")
    public List<Block> getAll() {
        return blockService.findAll();
    }

    @GetMapping("/page")
    @Operation(summary = "Lista paginata a caminelor")
    public Page<Block> getPaged(Pageable pageable) {
        return blockService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Camin dupa id")
    public Block getOne(@PathVariable Integer id) {
        return blockService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Creare camin")
    public ResponseEntity<Block> create(@Valid @RequestBody BlockDto dto,
                                        @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Block b = new Block();
        b.setName(dto.getName());
        return ResponseEntity.status(201).body(blockService.create(b, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizare camin")
    public Block update(@PathVariable Integer id, @Valid @RequestBody BlockDto dto,
                        @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Block b = new Block();
        b.setName(dto.getName());
        b.setEnabled(dto.getEnabled());
        return blockService.update(id, b, userId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Stergere logica camin")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        blockService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/requests")
    @Operation(summary = "Numar cereri per camin")
    public List<Object[]> requestsPerBlock() {
        return blockService.countRequestsPerBlock();
    }

    @GetMapping("/stats/students")
    @Operation(summary = "Numar studenti cazati per camin")
    public List<Object[]> studentsPerBlock() {
        return blockService.countStudentsPerBlock();
    }
}
