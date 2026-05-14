package com.campus.dormitory.service;

import com.campus.dormitory.client.UserClientService;
import com.campus.dormitory.client.UserDto;
import com.campus.dormitory.exception.BadRequestException;
import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.messaging.EventPublisher;
import com.campus.dormitory.model.*;
import com.campus.dormitory.repository.jpa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockRequestServiceTest {

    @Mock private BlockRequestRepository blockRequestRepository;
    @Mock private BedRepository bedRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private RentalAgreementRepository rentalAgreementRepository;
    @Mock private BlockRepository blockRepository;
    @Mock private PriceRepository priceRepository;
    @Mock private AuditLogService auditLogService;
    @Mock private UserClientService userClientService;
    @Mock private EventPublisher eventPublisher;

    @InjectMocks private BlockRequestService service;

    private Block block;
    private Bed bed;
    private Room room;
    private Price price;
    private BlockRequest pendingRequest;

    @BeforeEach
    void setUp() {
        block = new Block(); block.setId(10); block.setName("A1");

        room = new Room(); room.setId(100); room.setNumberSeats(2); room.setNumberSeatsoccupied(0);

        bed = new Bed(); bed.setId(200); bed.setEnabled(0); bed.setRoom(room);

        price = new Price(); price.setId(300); price.setPrice(500f);

        pendingRequest = new BlockRequest();
        pendingRequest.setId(1);
        pendingRequest.setBlock(block);
        pendingRequest.setUserId(42L);
        pendingRequest.setStatus(BlockRequestStatus.PENDING.name());
    }

    @Test
    void submit_persistsPendingRequestAndPublishesEvent() {
        when(blockRepository.findById(10)).thenReturn(Optional.of(block));
        when(userClientService.getUser(42L)).thenReturn(Optional.of(new UserDto(42L, "alice", "alice@a.com", "STUDENT")));
        when(blockRequestRepository.save(any(BlockRequest.class))).thenAnswer(inv -> {
            BlockRequest br = inv.getArgument(0);
            br.setId(1);
            return br;
        });

        BlockRequest saved = service.submit(10, 42L, 999L);

        assertEquals(BlockRequestStatus.PENDING.name(), saved.getStatus());
        assertEquals(42L, saved.getUserId());
        assertEquals(999L, saved.getContractId());
        verify(auditLogService).log(eq(42L), eq("SUBMIT"), eq("BlockRequest"), any(), any());
        verify(eventPublisher).publish(any(), any());
    }

    @Test
    void submit_unknownBlock_throwsResourceNotFound() {
        when(blockRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.submit(99, 42L, null));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void approve_pendingRequest_createsRentalAgreement() {
        when(blockRequestRepository.findBlockRequestById(1)).thenReturn(pendingRequest);
        when(bedRepository.findById(200)).thenReturn(Optional.of(bed));
        when(priceRepository.findById(300)).thenReturn(Optional.of(price));
        when(rentalAgreementRepository.save(any(RentalAgreement.class))).thenAnswer(inv -> {
            RentalAgreement r = inv.getArgument(0);
            r.setId(500);
            return r;
        });

        RentalAgreement ra = service.approve(1, 200, 300, 7L);

        assertEquals(BlockRequestStatus.APPROVED.name(), pendingRequest.getStatus());
        assertEquals(1, bed.getEnabled());
        assertEquals(1, room.getNumberSeatsoccupied());
        assertEquals(42L, ra.getUserId());
        assertEquals(500, ra.getId());
        verify(eventPublisher).publish(any(), any());
    }

    @Test
    void approve_alreadyApproved_throwsBadRequest() {
        pendingRequest.setStatus(BlockRequestStatus.APPROVED.name());
        when(blockRequestRepository.findBlockRequestById(1)).thenReturn(pendingRequest);

        assertThrows(BadRequestException.class, () -> service.approve(1, 200, 300, 7L));
        verifyNoInteractions(rentalAgreementRepository, eventPublisher);
    }

    @Test
    void approve_occupiedBed_throwsBadRequest() {
        bed.setEnabled(1);
        when(blockRequestRepository.findBlockRequestById(1)).thenReturn(pendingRequest);
        when(bedRepository.findById(200)).thenReturn(Optional.of(bed));

        assertThrows(BadRequestException.class, () -> service.approve(1, 200, 300, 7L));
        verifyNoInteractions(rentalAgreementRepository, eventPublisher);
    }

    @Test
    void reject_pendingRequest_setsRejectedStatus() {
        when(blockRequestRepository.findBlockRequestById(1)).thenReturn(pendingRequest);
        when(blockRequestRepository.save(any(BlockRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        BlockRequest result = service.reject(1, "no spots", 7L);

        assertEquals(BlockRequestStatus.REJECTED.name(), result.getStatus());
        assertEquals("no spots", result.getRejectionReason());
        verify(eventPublisher).publish(any(), any());
    }

    @Test
    void reject_alreadyRejected_throwsBadRequest() {
        pendingRequest.setStatus(BlockRequestStatus.REJECTED.name());
        when(blockRequestRepository.findBlockRequestById(1)).thenReturn(pendingRequest);

        assertThrows(BadRequestException.class, () -> service.reject(1, "x", 7L));
    }
}
