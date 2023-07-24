package com.jpmorgan.zhiyan.assignment.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoverRequestModel {
    @Nullable
    @JsonProperty("Rover Name to create")
    private String name;

    @JsonProperty("Starting Position")
    private PositionModel positionModel;
}
