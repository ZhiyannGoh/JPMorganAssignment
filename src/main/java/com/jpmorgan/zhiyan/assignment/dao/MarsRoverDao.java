package com.jpmorgan.zhiyan.assignment.dao;

import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MarsRoverDao extends JpaRepository<MarsRoverModel, Long> {
    List<MarsRoverModel> findByRoverName(String roverName);

    @Query(value = "SELECT * FROM mars_rover_model WHERE rover_name NOT IN ?1", nativeQuery = true)
    List<MarsRoverModel> findOtherRovers(List<String> roverName);

    @Modifying
    @Transactional
    @Query(value = "UPDATE mars_rover_model SET cardinal_direction=?2 WHERE rover_name=?1", nativeQuery = true)
    void updateCardinalDirection(String roverName, String updatedCardinalDirection);

    @Modifying
    @Transactional
    @Query(value = "UPDATE mars_rover_model SET x_axis=?2, y_axis=?3 WHERE rover_name=?1", nativeQuery = true)
    void updateCoordinate(String roverName, int xCoordinate, int yCoordinate);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO mars_rover_model (rover_name, x_axis, y_axis, cardinal_direction) VALUES (?1 ,?2 ,?3 ,?4)", nativeQuery = true)
    void insertRover(String roverName, int xCoordinate, int yCoordinate, String updatedCardinalDirection);

    @Modifying
    @Transactional
    @Query(value = "UPDATE mars_rover_model SET rover_name=?2 WHERE id=?1", nativeQuery = true)
    void updateRoverName(long id, String roverName);
}