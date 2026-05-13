package com.campus.dormitory.repository.jpa;

import com.campus.dormitory.model.Floor;
import com.campus.dormitory.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    Room findRoomById(int id);
    List<Room> findByFloor(Floor floor);
    List<Room> findByFloorAndEnabled(Floor floor, int enabled);
}
