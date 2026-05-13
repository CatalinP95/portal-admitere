package com.campus.dormitory.repository.jpa;

import com.campus.dormitory.model.Bed;
import com.campus.dormitory.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BedRepository extends JpaRepository<Bed, Integer> {

    List<Bed> findByRoom(Room room);
    List<Bed> findByRoomAndEnabled(Room room, int enabled);
}
