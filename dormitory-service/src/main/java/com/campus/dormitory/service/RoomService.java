package com.campus.dormitory.service;

import com.campus.dormitory.exception.ResourceNotFoundException;
import com.campus.dormitory.model.Floor;
import com.campus.dormitory.model.Room;
import com.campus.dormitory.repository.jpa.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final FloorService floorService;

    public RoomService(RoomRepository roomRepository, FloorService floorService) {
        this.roomRepository = roomRepository;
        this.floorService = floorService;
    }

    public List<Room> findAll() { return roomRepository.findAll(); }

    public Room findById(Integer id) {
        Room r = roomRepository.findRoomById(id);
        if (r == null) throw new ResourceNotFoundException("Room", id);
        return r;
    }

    public List<Room> findByFloorId(Integer floorId) {
        Floor floor = floorService.findById(floorId);
        return roomRepository.findByFloorAndEnabled(floor, 1);
    }

    @Transactional
    public Room create(String name, Integer type, Integer numberSeats, Integer floorId, Long userId) {
        Floor floor = floorService.findById(floorId);
        Room r = new Room();
        r.setName(name);
        r.setType(type);
        r.setNumberSeats(numberSeats);
        r.setNumberSeatsoccupied(0);
        r.setFloor(floor);
        r.setEnabled(1);
        r.setCreatedBy(userId);
        r.setCreatedAt(new Date());
        return roomRepository.save(r);
    }

    @Transactional
    public Room update(Integer id, String name, Integer type, Integer numberSeats, Integer floorId, Long userId) {
        Room existing = findById(id);
        if (name != null) existing.setName(name);
        if (type != null) existing.setType(type);
        if (numberSeats != null) existing.setNumberSeats(numberSeats);
        if (floorId != null) existing.setFloor(floorService.findById(floorId));
        existing.setModifiedBy(userId);
        existing.setModifiedAt(new Date());
        return roomRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Room existing = findById(id);
        existing.setEnabled(0);
        roomRepository.save(existing);
    }
}
