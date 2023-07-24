package com.jpmorgan.zhiyan.assignment.unit.util;

import com.jpmorgan.zhiyan.assignment.unit.BaseTest;
import com.jpmorgan.zhiyan.assignment.util.ExceptionUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ExceptionUtilTest extends BaseTest {

    @InjectMocks
    private ExceptionUtil exceptionUtil;

    @Test
    public void testCreateRoverNotFoundException() {
        String roverName = "UNKNOWN";
        try {
            exceptionUtil.createRoverNotFoundException(roverName);
            fail("ResponseStatusException caught");
        } catch (ResponseStatusException rse) {
            assertEquals(HttpStatus.NOT_FOUND, rse.getStatusCode());
            assertEquals("Rover " + roverName + " not found", rse.getReason());
        }
    }

    @Test
    public void testCreateCollisionDetectedDuringCreationException() {
        try {
            exceptionUtil.createCollisionDetectedDuringCreationException();
            fail("ResponseStatusException caught");
        } catch (ResponseStatusException rse) {
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
            assertEquals("Collision Detected. Rover is not created", rse.getReason());
        }
    }

    @Test
    public void testCreateOvercrowdedException() {
        try {
            exceptionUtil.createOvercrowdedException();
            fail("ResponseStatusException caught");
        } catch (ResponseStatusException rse) {
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
            assertEquals("Too much Rover in Mars", rse.getReason());
        }
    }

    @Test
    public void testCreateInvalidCardinalPositionException() {
        try {
            exceptionUtil.createInvalidCardinalPositionException("SOME_COMMAND");
            fail("ResponseStatusException caught");
        } catch (ResponseStatusException rse) {
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
            assertEquals("Cardinal Direction is only N/S/E/W. Invalid Cardinal Direction: SOME_COMMAND", rse.getReason());
        }
    }

    @Test
    public void testCreateInvalidRotationException() {
        try {
            exceptionUtil.createInvalidRotationException("SOME_COMMAND");
            fail("ResponseStatusException caught");
        } catch (ResponseStatusException rse) {
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
            assertEquals("Rover rotate either Right or Left. Invalid Rotation: SOME_COMMAND", rse.getReason());
        }
    }

    @Test
    public void testCreateInvalidMovementException() {
        try {
            exceptionUtil.createInvalidMovementException("SOME_COMMAND");
            fail("ResponseStatusException caught");
        } catch (ResponseStatusException rse) {
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
            assertEquals("Rover move either Forward or Backward. Invalid Movement: SOME_COMMAND", rse.getReason());
        }
    }

    @Test
    public void testCreateDataNotFoundNeededException() {
        try {
            exceptionUtil.createDataNotFoundNeededException("PLACEHOLDER_ROVER_NAME");
            fail("ResponseStatusException caught");
        } catch (ResponseStatusException rse) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rse.getStatusCode());
            assertEquals("Rover not inserted. Rover: PLACEHOLDER_ROVER_NAME", rse.getReason());
        }
    }

    @Test
    public void testCreateDatabaseReconciliationNeededException() {
        try {
            exceptionUtil.createDatabaseReconciliationNeededException("PLACEHOLDER_ROVER_NAME");
            fail("ResponseStatusException caught");
        } catch (ResponseStatusException rse) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rse.getStatusCode());
            assertEquals("There are 2 entries with the same name. Rover Placeholder Name: PLACEHOLDER_ROVER_NAME", rse.getReason());
        }
    }

    @Test
    public void testCreateRoverNameAlreadyExistException() {
        try {
            exceptionUtil.createRoverNameAlreadyExistException("DUPLICATE_ROVER_NAME");
            fail("ResponseStatusException caught");
        } catch (ResponseStatusException rse) {
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
            assertEquals("Rover name already exists. Rover Name: DUPLICATE_ROVER_NAME", rse.getReason());
        }
    }
}