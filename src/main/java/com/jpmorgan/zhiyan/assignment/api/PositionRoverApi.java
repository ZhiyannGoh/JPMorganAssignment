package com.jpmorgan.zhiyan.assignment.api;

import com.jpmorgan.zhiyan.assignment.handler.PositionRoverHandler;
import com.jpmorgan.zhiyan.assignment.model.response.PositionRoverResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PositionRoverApi {
    @Autowired
    PositionRoverHandler positionRoverHandler;

    @GetMapping(consumes = {"application/json"}, produces = {"application/json"}, path = {"rover/position"})
    @ResponseBody
    public PositionRoverResponseModel getRoverPosition(@RequestParam String roverName) {
        return positionRoverHandler.getRoverPosition(roverName);
    }
}