package com.mbld.jigslybackend.repositories.redis;

import com.mbld.jigslybackend.entities.Puzzle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuzzleRepository extends CrudRepository<Puzzle, String> {

}
