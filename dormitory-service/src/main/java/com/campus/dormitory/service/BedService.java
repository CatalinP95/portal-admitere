package com.campus.dormitory.service;

import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.model.Bed;
import com.campus.dormitory.model.Room;
import com.campus.dormitory.repository.jpa.BedRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BedService {

    private final BedRepository bedRepository;
    private final RoomService roomService;

    public BedService(BedRepository bedRepository, RoomService roomService) {
        this.bedRepository = bedRepository;
        this.roomService = roomService;
    }

    public List<Bed> findAll() { return bedRepository.findAll(); }

    public Bed findById(Integer id) {
        return bedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bed", id));
    }

    public List<Bed> findByRoomId(Integer roomId) {
        Room room = roomService.findById(roomId);
        return bedRepository.findByRoom(room);
    }

    @Transactional
    public Bed create(String name, Integer roomId, Long userId) {
        Room room = roomService.findById(roomId);
        Bed b = new Bed();
        b.setName(name);
        b.setRoom(room);
        // 0 = liber, 1 = ocupat (per legacy convention)
        b.setEnabled(0);
        b.setCreatedBy(userId);
        b.setCreatedAt(new Date());
        return bedRepository.save(b);
    }

    @Transactional
    public Bed update(Integer id, String name, Integer roomId, Long userId) {
        Bed existing = findById(id);
        if (name != null) existing.setName(name);
        if (roomId != null) existing.setRoom(roomService.findById(roomId));
        existing.setModifiedBy(userId);
        existing.setModifiedAt(new Date());
        return bedRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Bed existing = findById(id);
        bedRepository.delete(existing);
    }
}
