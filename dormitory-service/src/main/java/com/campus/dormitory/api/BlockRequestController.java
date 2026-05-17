package com.campus.dormitory.api;

import com.campus.dormitory.dto.ApproveRequestDto;
import com.campus.dormitory.dto.CreateBlockRequestDto;
import com.campus.dormitory.dto.RejectRequestDto;
import com.campus.dormitory.model.BlockRequest;
import com.campus.dormitory.model.RentalAgreement;
import com.campus.dormitory.service.BlockRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dormitory/requests")
@Tag(name = "BlockRequests", description = "Cereri cazare")
public class BlockRequestController {

    private final BlockRequestService blockRequestService;

    public BlockRequestController(BlockRequestService blockRequestService) {
        this.blockRequestService = blockRequestService;
    }

    @GetMapping
    public List<BlockRequest> getAll() {
        return blockRequestService.findAll();
    }

    @GetMapping("/{id}")
    public BlockRequest getOne(@PathVariable Integer id) {
        return blockRequestService.findById(id);
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Cereri filtrate dupa status")
    public Page<BlockRequest> getByStatus(@PathVariable String status, Pageable pageable) {
        return blockRequestService.findByStatus(status, pageable);
    }

    @GetMapping("/by-user/{userId}")
    public List<BlockRequest> getByUser(@PathVariable Long userId) {
        return blockRequestService.findByUserId(userId);
    }

    @PostMapping
    @Operation(summary = "Student depune cerere cazare")
    public ResponseEntity<BlockRequest> submit(@Valid @RequestBody CreateBlockRequestDto dto) {
        return ResponseEntity.status(201).body(
                blockRequestService.submit(dto.getBlockId(), dto.getUserId(), dto.getContractId()));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Admin aproba cerere si genereaza contract")
    public RentalAgreement approve(@PathVariable Integer id,
                                   @Valid @RequestBody ApproveRequestDto dto) {
        return blockRequestService.approve(id, dto.getBedId(), dto.getPriceId(), dto.getAdminId());
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Admin respinge cerere")
    public BlockRequest reject(@PathVariable Integer id,
                               @Valid @RequestBody RejectRequestDto dto) {
        return blockRequestService.reject(id, dto.getReason(), dto.getAdminId());
    }

    @GetMapping("/availability/free-seats")
    @Operation(summary = "Locuri libere per camera")
    public List<Object[]> freeSeats() {
        return blockRequestService.findFreeSeatsPerRoom();
    }

    @GetMapping("/availability/free-beds/{roomId}")
    @Operation(summary = "Paturi libere intr-o camera")
    public List<Object[]> freeBeds(@PathVariable Integer roomId) {
        return blockRequestService.findFreeBedsForRoom(roomId);
    }
}
