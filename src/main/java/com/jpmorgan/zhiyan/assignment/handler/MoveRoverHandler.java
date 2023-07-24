package com.jpmorgan.zhiyan.assignment.handler;

import com.jpmorgan.zhiyan.assignment.business.MoveRoverBusiness;
import com.jpmorgan.zhiyan.assignment.business.PositionRoverBusiness;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.request.MoveRoverRequestModel;
import com.jpmorgan.zhiyan.assignment.model.response.MoveRoverResponseModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

import static com.jpmorgan.zhiyan.assignment.model.PositionModel.MOVE_BACKWARD;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.MOVE_FORWARD;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.ROTATE_LEFT;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.ROTATE_RIGHT;

@Controller
public class MoveRoverHandler {
    private static final Logger logger = LoggerFactory.getLogger(MoveRoverHandler.class);
    private static final List<String> knownCommands = new ArrayList<>();

    static {
        knownCommands.add(MOVE_FORWARD);
        knownCommands.add(MOVE_BACKWARD);
        knownCommands.add(ROTATE_RIGHT);
        knownCommands.add(ROTATE_LEFT);
    }

    @Autowired
    MoveRoverBusiness moveRoverBusiness;

    @Autowired
    PositionRoverBusiness positionRoverBusiness;

    public MoveRoverResponseModel moveRover(MoveRoverRequestModel moveRoverRequestModel) {
        List<String> commandList = new ArrayList<>(Arrays.stream(moveRoverRequestModel.getCommands().split(",")).toList());
        Queue<String> knownCommandList = filterKnownCommands(commandList);

        String roverName = moveRoverRequestModel.getName();
        MarsRoverModel rover = positionRoverBusiness.getRoverPosition(roverName);

        String cancelledOperation = StringUtils.EMPTY;

        StringBuilder executedCommands = new StringBuilder();
        while (!knownCommandList.isEmpty()) {
            String currentCommand = knownCommandList.remove();
            try {
                executedCommands.append(currentCommand);
                move(rover, currentCommand);
            } catch (RuntimeException runtimeException) {
                cancelledOperation = knownCommandList.toString();
                break;
            }
        }

        return MoveRoverResponseModel.builder()
                .name(rover.getRoverName())
                .position(rover.getPosition())
                .requestCommand(commandList.toString())
                .commandExecuted(executedCommands.toString())
                .collisionDetected(cancelledOperation)
                .build();
    }

    private void move(MarsRoverModel marsRoverModel, String currentCommand) {
        adjustCardinal(marsRoverModel, currentCommand);
        adjustCoordinate(marsRoverModel, currentCommand);
    }

    private void adjustCoordinate(MarsRoverModel marsRoverModel, String currentCommand) {
        if (ROTATE_RIGHT.equalsIgnoreCase(currentCommand) || ROTATE_LEFT.equalsIgnoreCase(currentCommand)) {
            logger.info("Skipping due to adjust command");
            return;
        }
        moveRoverBusiness.move(marsRoverModel, currentCommand);
    }

    private void adjustCardinal(MarsRoverModel marsRoverModel, String currentCommand) {
        if (MOVE_FORWARD.equalsIgnoreCase(currentCommand) || MOVE_BACKWARD.equalsIgnoreCase(currentCommand)) {
            logger.info("Skipping due to move command");
            return;
        }
        moveRoverBusiness.rotate(marsRoverModel, currentCommand);
    }

    private Queue<String> filterKnownCommands(List<String> commandList) {
        return commandList.stream().filter(knownCommands::contains).collect(Collectors.toCollection(LinkedList::new));
    }
}