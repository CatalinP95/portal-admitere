package com.campus.dormitory.repository.jpa;

import com.campus.dormitory.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {

    List<Receipt> findByUserIdAndEnabled(Long userId, int enabled);
    Receipt findByUserIdAndEnabledAndDate(Long userId, int enabled, Date date);
    List<Receipt> findByPaymentStatus(String paymentStatus);
    List<Receipt> findByUserIdAndPaymentStatus(Long userId, String paymentStatus);

    @Query("SELECT r FROM receipt r WHERE r.paymentStatus = 'UNPAID' AND r.dueDate < :now")
    List<Receipt> findOverdueReceipts(@Param("now") Date now);
}
