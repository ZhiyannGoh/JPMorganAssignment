package com.jpmorgan.zhiyan.assignment.api;

import com.jpmorgan.zhiyan.assignment.handler.CreateRoverHandler;
import com.jpmorgan.zhiyan.assignment.model.request.CreateRoverRequestModel;
import com.jpmorgan.zhiyan.assignment.model.response.CreateRoverResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateRoverApi {
    @Autowired
    CreateRoverHandler createRoverHandler;

    @PostMapping(consumes = {"application/json"}, produces = {"application/json"}, path = {"rover/create"})
    @ResponseBody
    public CreateRoverResponseModel createRover(@RequestBody CreateRoverRequestModel createRoverRequestModel) {
        return createRoverHandler.createRover(createRoverRequestModel);
    }
}