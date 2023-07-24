package com.jpmorgan.zhiyan.assignment.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoverResponseModel {
    @JsonProperty("New Rover")
    private String name;

    @JsonProperty("Created Position")
    private PositionModel position;
}