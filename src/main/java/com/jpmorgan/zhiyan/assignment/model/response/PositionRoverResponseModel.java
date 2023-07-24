package com.jpmorgan.zhiyan.assignment.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionRoverResponseModel {
    @JsonProperty("Rover Name")
    private String name;

    @JsonProperty("Current Position")
    private PositionModel position;
}
