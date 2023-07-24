package com.jpmorgan.zhiyan.assignment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "mars_rover_model")
public class MarsRoverModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="rover_name", nullable = false)
    private String roverName;

    @Embedded
    private PositionModel position;
}
