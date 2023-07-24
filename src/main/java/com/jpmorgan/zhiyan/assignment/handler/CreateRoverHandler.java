package com.jpmorgan.zhiyan.assignment.handler;

import com.jpmorgan.zhiyan.assignment.business.CreateRoverBusiness;
import com.jpmorgan.zhiyan.assignment.business.PositionRoverBusiness;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.model.request.CreateRoverRequestModel;
import com.jpmorgan.zhiyan.assignment.model.response.CreateRoverResponseModel;
import com.jpmorgan.zhiyan.assignment.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CreateRoverHandler {
    @Autowired
    PositionRoverBusiness positionRoverBusiness;

    @Autowired
    CreateRoverBusiness createRoverBusiness;

    @Autowired
    ExceptionUtil exceptionUtil;

    public CreateRoverResponseModel createRover(CreateRoverRequestModel createRoverRequestModel) {
        MarsRoverModel marsRoverModel = mapToMarsRoverModel(createRoverRequestModel);

        if (positionRoverBusiness.determineCollision(marsRoverModel)) {
            exceptionUtil.createCollisionDetectedDuringCreationException();
        }

        MarsRoverModel createdRover = createRoverBusiness.createRover(marsRoverModel);
        return CreateRoverResponseModel.builder()
                .name(createdRover.getRoverName())
                .position(createdRover.getPosition())
                .build();
    }

    private MarsRoverModel mapToMarsRoverModel(CreateRoverRequestModel createRoverRequestModel) {
        PositionModel positionModel = mapShortToLongCardinal(createRoverRequestModel.getPositionModel());
        return MarsRoverModel.builder()
                .roverName(createRoverRequestModel.getName())
                .position(positionModel)
                .build();
    }

    private PositionModel mapShortToLongCardinal(PositionModel positionModel) {
        String shortCardinal = positionModel.getCardinal();
        String longCardinal = positionModel.getLongCardinal(shortCardinal);
        positionModel.setCardinal(longCardinal);
        return positionModel;
    }
}