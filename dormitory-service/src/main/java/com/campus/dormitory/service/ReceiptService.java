package com.campus.dormitory.service;

import com.campus.dormitory.config.RabbitConfig;
import com.campus.dormitory.exception.BadRequestException;
import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.messaging.DormitoryEvent;
import com.campus.dormitory.messaging.EventPublisher;
import com.campus.dormitory.model.PaymentStatus;
import com.campus.dormitory.model.Receipt;
import com.campus.dormitory.repository.jpa.ReceiptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final AuditLogService auditLogService;
    private final EventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    public ReceiptService(ReceiptRepository receiptRepository,
                          AuditLogService auditLogService,
                          EventPublisher eventPublisher) {
        this.receiptRepository = receiptRepository;
        this.auditLogService = auditLogService;
        this.eventPublisher = eventPublisher;
    }

    public List<Receipt> findAll() {
        return receiptRepository.findAll();
    }

    public Receipt findById(Integer id) {
        return receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", id));
    }

    public List<Receipt> findByUserId(Long userId) {
        return receiptRepository.findByUserIdAndEnabled(userId, 1);
    }

    public List<Receipt> findByUserIdAndStatus(Long userId, String status) {
        return receiptRepository.findByUserIdAndPaymentStatus(userId, status);
    }

    @Transactional
    public Receipt pay(Long userId, Integer receiptId) {
        Receipt receipt = findById(receiptId);
        if (!receipt.getUserId().equals(userId)) {
            throw new BadRequestException("Receipt does not belong to the requesting user");
        }
        if (PaymentStatus.PAID.name().equals(receipt.getPaymentStatus())) {
            throw new BadRequestException("Receipt is already PAID");
        }
        receipt.setPaymentStatus(PaymentStatus.PAID.name());
        receipt.setDataTranzactie(new Date());
        Receipt saved = receiptRepository.save(receipt);
        log.info("Receipt {} paid by user {}", receiptId, userId);
        auditLogService.log(userId, "PAY", "Receipt", String.valueOf(receiptId),
                "Receipt paid, amount " + saved.getAmount());
        eventPublisher.publish(RabbitConfig.RK_RECEIPT_PAID,
                new DormitoryEvent("RECEIPT_PAID", userId, receiptId,
                        "Amount " + saved.getAmount()));
        return saved;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void markOverdueAsLate() {
        log.info("Running overdue-receipts scheduler");
        receiptRepository.findOverdueReceipts(new Date()).forEach(r -> {
            r.setPaymentStatus(PaymentStatus.LATE.name());
            receiptRepository.save(r);
        });
    }

    @Scheduled(cron = "0 0 1 1 * *")
    @Transactional
    public void generateMonthlyReceipts() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        cal.add(Calendar.DAY_OF_MONTH, 15);
        Date dueDate = cal.getTime();
        Date now = new Date();
        log.info("Generating monthly receipts for {}/{}", month, year);

        entityManager.createNativeQuery(
            "insert into receipt (user_id, amount, month, year, due_date, date, payment_status, enabled, created_by, created_at, rentalagreement_id) " +
            "select ra.user_id, p.price, :month, :year, :dueDate, :now, 'UNPAID', 1, 0, :now, ra.id " +
            "from rentalagreement ra inner join price p on p.id = ra.price_id where ra.enabled = 1")
            .setParameter("month", month)
            .setParameter("year", year)
            .setParameter("dueDate", dueDate)
            .setParameter("now", now)
            .executeUpdate();
    }

    public Float findPriceForUser(Long userId) {
        try {
            return (Float) entityManager.createNativeQuery(
                    "select price.price from price " +
                    "INNER JOIN rentalagreement on price.id = rentalagreement.price_id " +
                    "and rentalagreement.user_id = :userId limit 1")
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (Exception ignored) {
            return 0f;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findTotalPaidForUser(Long userId) {
        return entityManager.createNativeQuery(
                "select count(receipt.id), price.price from receipt " +
                "INNER JOIN rentalagreement on receipt.rentalagreement_id = rentalagreement.id " +
                "and receipt.user_id = :userId " +
                "and receipt.payment_status = 'PAID' and receipt.enabled = 1 " +
                "INNER JOIN price on price.id = rentalagreement.price_id")
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Object[]> findBadStudents() {
        return entityManager.createNativeQuery(
                "select receipt.user_id from receipt " +
                "where receipt.enabled = 1 and receipt.payment_status = 'LATE' " +
                "GROUP BY receipt.user_id")
                .getResultList();
    }
}
