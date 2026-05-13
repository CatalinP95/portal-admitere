package com.campus.dormitory.service;

import com.campus.dormitory.exception.BadRequestException;
import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.model.*;
import com.campus.dormitory.repository.jpa.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Service
public class BlockRequestService {

    private static final Logger log = LoggerFactory.getLogger(BlockRequestService.class);

    private final BlockRequestRepository blockRequestRepository;
    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final RentalAgreementRepository rentalAgreementRepository;
    private final BlockRepository blockRepository;
    private final PriceRepository priceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public BlockRequestService(BlockRequestRepository blockRequestRepository,
                               BedRepository bedRepository,
                               RoomRepository roomRepository,
                               RentalAgreementRepository rentalAgreementRepository,
                               BlockRepository blockRepository,
                               PriceRepository priceRepository) {
        this.blockRequestRepository = blockRequestRepository;
        this.bedRepository = bedRepository;
        this.roomRepository = roomRepository;
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.blockRepository = blockRepository;
        this.priceRepository = priceRepository;
    }

    public List<BlockRequest> findAll() {
        return blockRequestRepository.findAll();
    }

    public BlockRequest findById(Integer id) {
        BlockRequest req = blockRequestRepository.findBlockRequestById(id);
        if (req == null) throw new ResourceNotFoundException("BlockRequest", id);
        return req;
    }

    public List<BlockRequest> findByUserId(Long userId) {
        return blockRequestRepository.findByUserIdAndEnabled(userId, 1);
    }

    public List<BlockRequest> findByStatus(String status) {
        return blockRequestRepository.findByStatus(status);
    }

    public Page<BlockRequest> findByStatus(String status, Pageable pageable) {
        return blockRequestRepository.findByStatus(status, pageable);
    }

    @Transactional
    public BlockRequest submit(Integer blockId, Long userId, Long contractId) {
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new ResourceNotFoundException("Block", blockId));

        Date now = new Date();
        BlockRequest req = new BlockRequest();
        req.setBlock(block);
        req.setUserId(userId);
        req.setContractId(contractId);
        req.setStatus(BlockRequestStatus.PENDING.name());
        req.setDate(now);
        req.setEnabled(1);
        req.setCreatedBy(userId);
        req.setCreatedAt(now);
        BlockRequest saved = blockRequestRepository.save(req);
        log.info("Block request {} submitted by user {}", saved.getId(), userId);
        return saved;
    }

    @Transactional
    public RentalAgreement approve(Integer blockRequestId, Integer bedId, Integer priceId, Long adminId) {
        BlockRequest req = findById(blockRequestId);
        if (!BlockRequestStatus.PENDING.name().equals(req.getStatus())) {
            throw new BadRequestException("Doar cererile PENDING pot fi aprobate");
        }

        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed", bedId));
        if (bed.getEnabled() != null && bed.getEnabled() == 1) {
            throw new BadRequestException("Patul " + bedId + " este deja ocupat");
        }

        Price price = priceRepository.findById(priceId)
                .orElseThrow(() -> new ResourceNotFoundException("Price", priceId));

        Date now = new Date();
        req.setStatus(BlockRequestStatus.APPROVED.name());
        req.setModifiedBy(adminId);
        req.setModifiedAt(now);
        blockRequestRepository.save(req);

        bed.setEnabled(1);
        bed.setModifiedBy(adminId);
        bed.setModifiedAt(now);
        bedRepository.save(bed);

        Room room = bed.getRoom();
        if (room.getNumberSeatsoccupied() == null) room.setNumberSeatsoccupied(0);
        room.setNumberSeatsoccupied(room.getNumberSeatsoccupied() + 1);
        roomRepository.save(room);

        RentalAgreement ra = new RentalAgreement();
        ra.setBlockRequest(req);
        ra.setBed(bed);
        ra.setPrice(price);
        ra.setUserId(req.getUserId());
        ra.setDate(now);
        ra.setEnabled(1);
        ra.setCreatedBy(adminId);
        ra.setCreatedAt(now);
        RentalAgreement savedRa = rentalAgreementRepository.save(ra);

        log.info("Block request {} approved by admin {}, rental agreement {} created",
                blockRequestId, adminId, savedRa.getId());
        return savedRa;
    }

    @Transactional
    public BlockRequest reject(Integer blockRequestId, String reason, Long adminId) {
        BlockRequest req = findById(blockRequestId);
        if (!BlockRequestStatus.PENDING.name().equals(req.getStatus())) {
            throw new BadRequestException("Doar cererile PENDING pot fi respinse");
        }
        req.setStatus(BlockRequestStatus.REJECTED.name());
        req.setRejectionReason(reason);
        req.setModifiedBy(adminId);
        req.setModifiedAt(new Date());
        BlockRequest saved = blockRequestRepository.save(req);
        log.info("Block request {} rejected by admin {}: {}", blockRequestId, adminId, reason);
        return saved;
    }

    public List<Object[]> findFreeSeatsPerRoom() {
        return entityManager.createNativeQuery(
                "select room.id as room, room.number_seats as total, " +
                "room.number_seats - room.number_seatsoccupied as free, room.type, block.id " +
                "from room " +
                "inner join floor on floor.id = room.floor_id " +
                "inner join block on block.id = floor.block_id")
                .getResultList();
    }

    public List<Object[]> findFreeBedsForRoom(Integer roomId) {
        return entityManager.createNativeQuery(
                "select bed.id, room.type from bed " +
                "INNER JOIN room on room.id = bed.room_id " +
                "where bed.enabled = 0 and bed.room_id = :roomId")
                .setParameter("roomId", roomId)
                .getResultList();
    }
}
