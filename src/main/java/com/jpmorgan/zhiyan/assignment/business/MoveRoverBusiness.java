package com.jpmorgan.zhiyan.assignment.business;

import com.jpmorgan.zhiyan.assignment.dao.MarsRoverDao;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.jpmorgan.zhiyan.assignment.model.PositionModel.*;

@Service
public class MoveRoverBusiness {
    private static final Logger logger = LoggerFactory.getLogger(MoveRoverBusiness.class);

    private static final String Y_AXIS_NORTH_FORWARD = "Y_AXIS_NORTH_FORWARD";

    private static final String Y_AXIS_NORTH_BACKWARD = "Y_AXIS_NORTH_BACKWARD";

    private static final String Y_AXIS_SOUTH_FORWARD = "Y_AXIS_SOUTH_FORWARD";

    private static final String Y_AXIS_SOUTH_BACKWARD = "Y_AXIS_SOUTH_BACKWARD";

    private static final String X_AXIS_EAST_FORWARD = "X_AXIS_EAST_FORWARD";

    private static final String X_AXIS_EAST_BACKWARD = "X_AXIS_EAST_BACKWARD";

    private static final String X_AXIS_WEST_FORWARD = "X_AXIS_WEST_FORWARD";

    private static final String X_AXIS_WEST_BACKWARD = "X_AXIS_WEST_BACKWARD";

    @Autowired
    MarsRoverDao marsRoverDao;

    @Autowired
    ExceptionUtil exceptionUtil;

    @Autowired
    PositionRoverBusiness positionRoverBusiness;

    protected static final Map<String, String> rotateRightMap = new HashMap<>();

    static {
        rotateRightMap.put(LONG_CARDINAL_NORTH, LONG_CARDINAL_EAST);
        rotateRightMap.put(LONG_CARDINAL_SOUTH, LONG_CARDINAL_WEST);
        rotateRightMap.put(LONG_CARDINAL_EAST, LONG_CARDINAL_SOUTH);
        rotateRightMap.put(LONG_CARDINAL_WEST, LONG_CARDINAL_NORTH);
    }

    protected static final Map<String, String> rotateLeftMap = new HashMap<>();

    static {
        rotateLeftMap.put(LONG_CARDINAL_NORTH, LONG_CARDINAL_WEST);
        rotateLeftMap.put(LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST);
        rotateLeftMap.put(LONG_CARDINAL_EAST, LONG_CARDINAL_NORTH);
        rotateLeftMap.put(LONG_CARDINAL_WEST, LONG_CARDINAL_SOUTH);
    }

    protected static final Map<String, Integer> moveCoordinate = new HashMap<>();

    static {
        moveCoordinate.put(Y_AXIS_NORTH_FORWARD, 1);
        moveCoordinate.put(Y_AXIS_NORTH_BACKWARD, -1);

        moveCoordinate.put(Y_AXIS_SOUTH_FORWARD, -1);
        moveCoordinate.put(Y_AXIS_SOUTH_BACKWARD, 1);

        moveCoordinate.put(X_AXIS_EAST_FORWARD, 1);
        moveCoordinate.put(X_AXIS_EAST_BACKWARD, -1);

        moveCoordinate.put(X_AXIS_WEST_FORWARD, -1);
        moveCoordinate.put(X_AXIS_WEST_BACKWARD, 1);
    }

    public void rotate(MarsRoverModel marsRoverModel, String currentCommand) {
        PositionModel positionModel = marsRoverModel.getPosition();
        String currentPosition = positionModel.getCardinal();

        String updatedCardinalDirection = StringUtils.EMPTY;
        if (ROTATE_RIGHT.equalsIgnoreCase(currentCommand)) {
            updatedCardinalDirection = rotateRightMap.getOrDefault(currentPosition, StringUtils.EMPTY);
        } else if (ROTATE_LEFT.equalsIgnoreCase(currentCommand)) {
            updatedCardinalDirection = rotateLeftMap.getOrDefault(currentPosition, StringUtils.EMPTY);
        } else {
            exceptionUtil.createInvalidRotationException(currentCommand);
        }

        if (StringUtils.isEmpty(updatedCardinalDirection)) {
            exceptionUtil.createInvalidCardinalPositionException(updatedCardinalDirection);
        }

        marsRoverDao.updateCardinalDirection(marsRoverModel.getRoverName(), updatedCardinalDirection);
        updateMarsRoverCardinalDirection(marsRoverModel, updatedCardinalDirection);
    }

    private void updateMarsRoverCardinalDirection(MarsRoverModel marsRoverModel, String updatedPosition) {
        PositionModel positionModel = marsRoverModel.getPosition();
        positionModel.setCardinal(updatedPosition);
        marsRoverModel.setPosition(positionModel);
    }

    public void move(MarsRoverModel marsRoverModel, String currentCommand) {
        PositionModel positionModel = marsRoverModel.getPosition();
        String roverCardinalDirection = positionModel.getCardinal();
        if (MOVE_FORWARD.equalsIgnoreCase(currentCommand)) {
            moveForward(marsRoverModel, roverCardinalDirection);
        } else if (MOVE_BACKWARD.equalsIgnoreCase(currentCommand)) {
            moveBackward(marsRoverModel, roverCardinalDirection);
        } else {
            exceptionUtil.createInvalidMovementException(currentCommand);
        }
        marsRoverDao.updateCoordinate(marsRoverModel.getRoverName(), positionModel.getXCoordinate(), positionModel.getYCoordinate());
    }

    private void moveForward(MarsRoverModel marsRoverModel, String roverCardinalDirection) {
        int offset;
        PositionModel positionModel = marsRoverModel.getPosition();

        //Update North/South
        if (LONG_CARDINAL_NORTH.equalsIgnoreCase(roverCardinalDirection) || (LONG_CARDINAL_SOUTH.equalsIgnoreCase(roverCardinalDirection))) {
            if (LONG_CARDINAL_NORTH.equalsIgnoreCase(roverCardinalDirection)) {
                offset = moveCoordinate.get(Y_AXIS_NORTH_FORWARD);
            } else {
                offset = moveCoordinate.get(Y_AXIS_SOUTH_FORWARD);
            }
            updateOrFailYAxisOperation(marsRoverModel, offset, positionModel);

            //Update East/West
        } else if (LONG_CARDINAL_EAST.equalsIgnoreCase(roverCardinalDirection) || LONG_CARDINAL_WEST.equalsIgnoreCase(roverCardinalDirection)) {
            if (LONG_CARDINAL_EAST.equalsIgnoreCase(roverCardinalDirection)) {
                offset = moveCoordinate.get(X_AXIS_EAST_FORWARD);
            } else {
                offset = moveCoordinate.get(X_AXIS_WEST_FORWARD);
            }
            updateOrFailXAxisOperation(marsRoverModel, offset, positionModel);

        } else {
            exceptionUtil.createInvalidCardinalPositionException(roverCardinalDirection);
        }
    }

    private void moveBackward(MarsRoverModel marsRoverModel, String roverCardinalDirection) {
        PositionModel positionModel = marsRoverModel.getPosition();

        //Update North/South
        if (LONG_CARDINAL_NORTH.equalsIgnoreCase(roverCardinalDirection) || (LONG_CARDINAL_SOUTH.equalsIgnoreCase(roverCardinalDirection))) {
            int offset;
            if (LONG_CARDINAL_NORTH.equalsIgnoreCase(roverCardinalDirection)) {
                offset = moveCoordinate.get(Y_AXIS_NORTH_BACKWARD);
            } else {
                offset = moveCoordinate.get(Y_AXIS_SOUTH_BACKWARD);
            }
            updateOrFailYAxisOperation(marsRoverModel, offset, positionModel);

            //Update East/West
        } else if (LONG_CARDINAL_EAST.equalsIgnoreCase(roverCardinalDirection) || (LONG_CARDINAL_WEST.equalsIgnoreCase(roverCardinalDirection))) {
            int offset;
            if (LONG_CARDINAL_EAST.equalsIgnoreCase(roverCardinalDirection)) {
                offset = moveCoordinate.get(X_AXIS_EAST_BACKWARD);
            } else {
                offset = moveCoordinate.get(X_AXIS_WEST_BACKWARD);
            }
            updateOrFailXAxisOperation(marsRoverModel, offset, positionModel);

        } else {
            exceptionUtil.createInvalidCardinalPositionException(roverCardinalDirection);
        }
    }

    private void updateOrFailYAxisOperation(MarsRoverModel marsRoverModel, int offset, PositionModel positionModel) {
        int updatedRoverYAxis = calculateCoordinate(positionModel.getYCoordinate(), offset);
        checkRoverCollisionYAxis(marsRoverModel, updatedRoverYAxis);
        updateYAxis(marsRoverModel, updatedRoverYAxis);
    }

    private void updateOrFailXAxisOperation(MarsRoverModel marsRoverModel, int offset, PositionModel positionModel) {
        int updatedRoverXAxis = calculateCoordinate(positionModel.getXCoordinate(), offset);
        checkRoverCollisionXAxis(marsRoverModel, updatedRoverXAxis);
        updateXAxis(marsRoverModel, updatedRoverXAxis);
    }

    private void updateYAxis(MarsRoverModel marsRoverModel, int updatedRoverYAxis) {
        PositionModel positionModel = marsRoverModel.getPosition();
        positionModel.setYCoordinate(updatedRoverYAxis);
        marsRoverModel.setPosition(positionModel);
    }

    private void updateXAxis(MarsRoverModel marsRoverModel, int updatedRoverXAxis) {
        PositionModel positionModel = marsRoverModel.getPosition();
        positionModel.setXCoordinate(updatedRoverXAxis);
        marsRoverModel.setPosition(positionModel);
    }

    private void checkRoverCollisionXAxis(MarsRoverModel marsRoverModel, int updatedRoverXAxis) {
        checkRoverCollision(marsRoverModel, Optional.of(updatedRoverXAxis), Optional.empty());
    }

    private void checkRoverCollisionYAxis(MarsRoverModel marsRoverModel, int updatedRoverYAxis) {
        checkRoverCollision(marsRoverModel, Optional.empty(), Optional.of(updatedRoverYAxis));
    }


    private void checkRoverCollision(MarsRoverModel actualMarsRoverModel, Optional<Integer> xAxis, Optional<Integer> yAxis) {
        PositionModel actualPositionModel = actualMarsRoverModel.getPosition();
        MarsRoverModel collisionChecker = MarsRoverModel.builder()
                .roverName(actualMarsRoverModel.getRoverName())
                .position(PositionModel.builder()
                        .cardinal(actualPositionModel.getCardinal())
                        .build())
                .build();
        PositionModel positionChecker = collisionChecker.getPosition();

        if (xAxis.isPresent()) {
            positionChecker.setXCoordinate(xAxis.get());
        } else {
            positionChecker.setXCoordinate(actualPositionModel.getXCoordinate());
        }

        if (yAxis.isPresent()) {
            positionChecker.setYCoordinate(yAxis.get());
        } else {
            positionChecker.setYCoordinate(actualPositionModel.getYCoordinate());
        }

        if (positionRoverBusiness.determineCollisionOtherWithRoverName(collisionChecker)) {
            throw new RuntimeException("Collision Detected");
        }
    }

    private int calculateCoordinate(int currentCoordinate, int offset) {
        return currentCoordinate + offset;
    }
}