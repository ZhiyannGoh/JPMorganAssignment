package com.jpmorgan.zhiyan.assignment.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoveRoverResponseModel {
    @JsonProperty("Rover Name")
    private String name;

    @JsonProperty("Final Position")
    private PositionModel position;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("Command(s) in Request")
    private String requestCommand;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("Command(s) executed")
    private String commandExecuted;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("Command(s) not executed after Collision")
    private String collisionDetected;
}