package com.jpmorgan.zhiyan.assignment.handler;

import com.jpmorgan.zhiyan.assignment.business.PositionRoverBusiness;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.response.PositionRoverResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PositionRoverHandler {
    @Autowired
    PositionRoverBusiness positionRoverBusiness;

    public PositionRoverResponseModel getRoverPosition(String roverName) {
        MarsRoverModel rover = positionRoverBusiness.getRoverPosition(roverName);
        return PositionRoverResponseModel.builder()
                .name(rover.getRoverName())
                .position(rover.getPosition())
                .build();
    }
}