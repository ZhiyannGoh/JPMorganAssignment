package com.jpmorgan.zhiyan.assignment.api;

import com.jpmorgan.zhiyan.assignment.handler.MoveRoverHandler;
import com.jpmorgan.zhiyan.assignment.model.request.MoveRoverRequestModel;
import com.jpmorgan.zhiyan.assignment.model.response.MoveRoverResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MoveRoverApi {
    @Autowired
    MoveRoverHandler moveRoverHandler;

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"}, path = {"rover/move"})
    @ResponseBody
    public MoveRoverResponseModel moveRover(@RequestBody MoveRoverRequestModel moveRoverRequestModel) {
        return moveRoverHandler.moveRover(moveRoverRequestModel);
    }
}
