package com.jpmorgan.zhiyan.assignment.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoveRoverRequestModel {
    @NonNull
    @JsonProperty("Rover Name")
    private String name;

    @NonNull
    @JsonProperty("Rover Commands")
    private String commands;
}