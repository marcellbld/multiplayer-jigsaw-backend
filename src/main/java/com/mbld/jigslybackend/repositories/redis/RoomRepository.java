package com.mbld.jigslybackend.repositories.redis;


import com.mbld.jigslybackend.entities.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CrudRepository<Room, String> {
}
