package com.jpmorgan.zhiyan.assignment.util;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ExceptionUtil {
    public void createRoverNotFoundException(String roverName) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Rover %s not found", roverName));
    }

    public void createCollisionDetectedDuringCreationException() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collision Detected. Rover is not created");
    }

    public void createOvercrowdedException() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too much Rover in Mars");
    }

    public void createInvalidCardinalPositionException(String command) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Cardinal Direction is only N/S/E/W. Invalid Cardinal Direction: %s", command));
    }

    public void createDatabaseReconciliationNeededException(String roverPlaceholderName) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("There are 2 entries with the same name. Rover Placeholder Name: %s", roverPlaceholderName));
    }

    public void createDataNotFoundNeededException(String roverPlaceholderName) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("Rover not inserted. Rover: %s", roverPlaceholderName));
    }

    public void createInvalidRotationException(String command) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Rover rotate either Right or Left. Invalid Rotation: %s", command));
    }

    public void createInvalidMovementException(String command) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Rover move either Forward or Backward. Invalid Movement: %s", command));
    }

    public void createRoverNameAlreadyExistException(String roverName) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Rover name already exists. Rover Name: %s", roverName));
    }
}
