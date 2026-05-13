package com.campus.dormitory.api;

import com.campus.dormitory.model.Receipt;
import com.campus.dormitory.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dormitory/receipts")
@Tag(name = "Receipts", description = "Facturi cazare")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    public List<Receipt> getAll() { return receiptService.findAll(); }

    @GetMapping("/{id}")
    public Receipt getOne(@PathVariable Integer id) { return receiptService.findById(id); }

    @GetMapping("/by-user/{userId}")
    public List<Receipt> getByUser(@PathVariable Long userId) {
        return receiptService.findByUserId(userId);
    }

    @GetMapping("/by-user/{userId}/status/{status}")
    public List<Receipt> getByUserAndStatus(@PathVariable Long userId, @PathVariable String status) {
        return receiptService.findByUserIdAndStatus(userId, status);
    }

    @PostMapping("/{receiptId}/pay")
    @Operation(summary = "Student plateste o factura")
    public Receipt pay(@PathVariable Integer receiptId,
                       @RequestHeader("X-User-Id") Long userId) {
        return receiptService.pay(userId, receiptId);
    }

    @GetMapping("/stats/bad-students")
    public List<Object[]> badStudents() {
        return receiptService.findBadStudents();
    }

    @GetMapping("/stats/total-paid/{userId}")
    public List<Object[]> totalPaid(@PathVariable Long userId) {
        return receiptService.findTotalPaidForUser(userId);
    }

    @GetMapping("/stats/price/{userId}")
    public Float currentPrice(@PathVariable Long userId) {
        return receiptService.findPriceForUser(userId);
    }
}
