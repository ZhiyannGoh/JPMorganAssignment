package com.jpmorgan.zhiyan.assignment.unit.business;

import com.jpmorgan.zhiyan.assignment.business.MoveRoverBusiness;
import com.jpmorgan.zhiyan.assignment.business.PositionRoverBusiness;
import com.jpmorgan.zhiyan.assignment.dao.MarsRoverDao;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.unit.BaseTest;
import com.jpmorgan.zhiyan.assignment.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static com.jpmorgan.zhiyan.assignment.model.PositionModel.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class MoveRoverBusinessTest extends BaseTest {
    @InjectMocks
    private MoveRoverBusiness moveRoverBusiness;

    @Mock
    private MarsRoverDao marsRoverDao;

    @Mock
    private ExceptionUtil exceptionUtil;

    @Mock
    private PositionRoverBusiness positionRoverBusiness;

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testRotateRight(String input) {
        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        marsRoverModelSpy.setPosition(positionModelSpy);

        String expectedCardinal = StringUtils.EMPTY;
        switch (input) {
            case LONG_CARDINAL_NORTH -> expectedCardinal = LONG_CARDINAL_EAST;
            case LONG_CARDINAL_SOUTH -> expectedCardinal = LONG_CARDINAL_WEST;
            case LONG_CARDINAL_EAST -> expectedCardinal = LONG_CARDINAL_SOUTH;
            case LONG_CARDINAL_WEST -> expectedCardinal = LONG_CARDINAL_NORTH;
            default -> {
            }
        }

        moveRoverBusiness.rotate(marsRoverModelSpy, ROTATE_RIGHT);
        verify(marsRoverDao, atMostOnce()).updateCardinalDirection(anyString(), anyString());
        assertEquals(expectedCardinal, positionModelSpy.getCardinal());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testRotateLeft(String input) {
        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        marsRoverModelSpy.setPosition(positionModelSpy);

        String expectedCardinal = StringUtils.EMPTY;
        switch (input) {
            case LONG_CARDINAL_NORTH -> expectedCardinal = LONG_CARDINAL_WEST;
            case LONG_CARDINAL_SOUTH -> expectedCardinal = LONG_CARDINAL_EAST;
            case LONG_CARDINAL_EAST -> expectedCardinal = LONG_CARDINAL_NORTH;
            case LONG_CARDINAL_WEST -> expectedCardinal = LONG_CARDINAL_SOUTH;
            default -> {
            }
        }

        moveRoverBusiness.rotate(marsRoverModelSpy, ROTATE_LEFT);
        verify(marsRoverDao, atMostOnce()).updateCardinalDirection(anyString(), anyString());
        assertEquals(expectedCardinal, positionModelSpy.getCardinal());
    }

    @Test
    public void testRotateInvalidRotateCommand() {
        MarsRoverModel marsRoverModelMock = mock(MarsRoverModel.class);
        PositionModel positionModelMock = mock(PositionModel.class);
        when(marsRoverModelMock.getPosition()).thenReturn(positionModelMock);
        when(positionModelMock.getCardinal()).thenReturn(StringUtils.EMPTY);

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(exceptionUtil).createInvalidRotationException(anyString());

        try {
            moveRoverBusiness.rotate(marsRoverModelMock, "ROTATE_UNKNOWN");
            fail("Exception Caught");
        } catch (ResponseStatusException rse) {
            verify(marsRoverDao, never()).updateCardinalDirection(anyString(), anyString());
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
        }
    }

    @Test
    public void testRotateInvalidCardinalDirection() {
        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal("CARDINAL_UNKNOWN");
        marsRoverModelSpy.setPosition(positionModelSpy);

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(exceptionUtil).createInvalidCardinalPositionException(anyString());

        try {
            moveRoverBusiness.rotate(marsRoverModelSpy, ROTATE_RIGHT);
            fail("Exception Caught");
        } catch (ResponseStatusException rse) {
            verify(marsRoverDao, never()).updateCardinalDirection(anyString(), anyString());
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveForward(String input) {
        int x = 1;
        int y = 2;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(marsRoverDao.findAll()).thenReturn(Collections.emptyList());
        when(positionRoverBusiness.determineCollision(any())).thenReturn(false);

        switch (input) {
            case LONG_CARDINAL_NORTH -> y = 3;
            case LONG_CARDINAL_SOUTH -> y = 1;
            case LONG_CARDINAL_EAST -> x = 2;
            case LONG_CARDINAL_WEST -> x = 0;
            default -> {
            }
        }

        moveRoverBusiness.move(marsRoverModelSpy, MOVE_FORWARD);
        verify(marsRoverDao, atMostOnce()).updateCoordinate(anyString(),anyInt(), anyInt());
        verify(marsRoverDao, atMostOnce()).findByRoverName(any());
        assertEquals(x, positionModelSpy.getXCoordinate());
        assertEquals(y, positionModelSpy.getYCoordinate());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveForwardWithXEqual0AndYEqual0(String input) {
        int x = 0;
        int y = 0;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(marsRoverDao.findAll()).thenReturn(Collections.emptyList());
        when(positionRoverBusiness.determineCollision(any())).thenReturn(false);

        switch (input) {
            case LONG_CARDINAL_NORTH -> y = 1;
            case LONG_CARDINAL_SOUTH -> y = -1;
            case LONG_CARDINAL_EAST -> x = 1;
            case LONG_CARDINAL_WEST -> x = -1;
            default -> {
            }
        }

        moveRoverBusiness.move(marsRoverModelSpy, MOVE_FORWARD);
        verify(marsRoverDao, atMostOnce()).updateCoordinate(anyString(),anyInt(), anyInt());
        verify(marsRoverDao, atMostOnce()).findByRoverName(any());
        assertEquals(x, positionModelSpy.getXCoordinate());
        assertEquals(y, positionModelSpy.getYCoordinate());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveForwardWithXEqualNegative1AndYEqualNegative1(String input) {
        int x = -1;
        int y = -1;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(marsRoverDao.findAll()).thenReturn(Collections.emptyList());
        when(positionRoverBusiness.determineCollision(any())).thenReturn(false);

        switch (input) {
            case LONG_CARDINAL_NORTH -> y = 0;
            case LONG_CARDINAL_SOUTH -> y = -2;
            case LONG_CARDINAL_EAST -> x = 0;
            case LONG_CARDINAL_WEST -> x = -2;
            default -> {
            }
        }

        moveRoverBusiness.move(marsRoverModelSpy, MOVE_FORWARD);
        verify(marsRoverDao, atMostOnce()).updateCoordinate(anyString(),anyInt(), anyInt());
        verify(marsRoverDao, atMostOnce()).findByRoverName(any());
        assertEquals(x, positionModelSpy.getXCoordinate());
        assertEquals(y, positionModelSpy.getYCoordinate());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveForwardWithXEqual1AndYEqual1(String input) {
        int x = 1;
        int y = 1;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(marsRoverDao.findAll()).thenReturn(Collections.emptyList());
        when(positionRoverBusiness.determineCollision(any())).thenReturn(false);

        switch (input) {
            case LONG_CARDINAL_NORTH -> y = 2;
            case LONG_CARDINAL_SOUTH -> y = 0;
            case LONG_CARDINAL_EAST -> x = 2;
            case LONG_CARDINAL_WEST -> x = 0;
            default -> {
            }
        }

        moveRoverBusiness.move(marsRoverModelSpy, MOVE_FORWARD);
        verify(marsRoverDao, atMostOnce()).updateCoordinate(anyString(),anyInt(), anyInt());
        verify(marsRoverDao, atMostOnce()).findByRoverName(any());
        assertEquals(x, positionModelSpy.getXCoordinate());
        assertEquals(y, positionModelSpy.getYCoordinate());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveBackwardWithXEqual0AndYEqual0(String input) {
        int x = 0;
        int y = 0;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(marsRoverDao.findAll()).thenReturn(Collections.emptyList());
        when(positionRoverBusiness.determineCollision(any())).thenReturn(false);

        switch (input) {
            case LONG_CARDINAL_NORTH -> y = -1;
            case LONG_CARDINAL_SOUTH -> y = 1;
            case LONG_CARDINAL_EAST -> x = -1;
            case LONG_CARDINAL_WEST -> x = 1;
            default -> {
            }
        }

        moveRoverBusiness.move(marsRoverModelSpy, MOVE_BACKWARD);
        verify(marsRoverDao, atMostOnce()).updateCoordinate(anyString(),anyInt(), anyInt());
        verify(marsRoverDao, atMostOnce()).findByRoverName(any());
        assertEquals(x, positionModelSpy.getXCoordinate());
        assertEquals(y, positionModelSpy.getYCoordinate());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveBackwardWithXEqual1AndYEqual1(String input) {
        int x = 1;
        int y = 1;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(marsRoverDao.findAll()).thenReturn(Collections.emptyList());
        when(positionRoverBusiness.determineCollision(any())).thenReturn(false);

        switch (input) {
            case LONG_CARDINAL_NORTH -> y = 0;
            case LONG_CARDINAL_SOUTH -> y = 2;
            case LONG_CARDINAL_EAST -> x = 0;
            case LONG_CARDINAL_WEST -> x = 2;
            default -> {
            }
        }

        moveRoverBusiness.move(marsRoverModelSpy, MOVE_BACKWARD);
        verify(marsRoverDao, atMostOnce()).updateCoordinate(anyString(),anyInt(), anyInt());
        verify(marsRoverDao, atMostOnce()).findByRoverName(any());
        assertEquals(x, positionModelSpy.getXCoordinate());
        assertEquals(y, positionModelSpy.getYCoordinate());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveBackwardWithXEqualNegative1AndYEqualNegative1(String input) {
        int x = -1;
        int y = -1;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(marsRoverDao.findAll()).thenReturn(Collections.emptyList());
        when(positionRoverBusiness.determineCollision(any())).thenReturn(false);

        switch (input) {
            case LONG_CARDINAL_NORTH -> y = -2;
            case LONG_CARDINAL_SOUTH -> y = 0;
            case LONG_CARDINAL_EAST -> x = -2;
            case LONG_CARDINAL_WEST -> x = 0;
            default -> {
            }
        }

        moveRoverBusiness.move(marsRoverModelSpy, MOVE_BACKWARD);
        verify(marsRoverDao, atMostOnce()).updateCoordinate(anyString(),anyInt(), anyInt());
        verify(marsRoverDao, atMostOnce()).findByRoverName(any());
        assertEquals(x, positionModelSpy.getXCoordinate());
        assertEquals(y, positionModelSpy.getYCoordinate());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveBackward(String input) {
        int x = 2;
        int y = 1;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(marsRoverDao.findAll()).thenReturn(Collections.emptyList());
        when(positionRoverBusiness.determineCollision(any())).thenReturn(false);

        switch (input) {
            case LONG_CARDINAL_NORTH -> y = 0;
            case LONG_CARDINAL_SOUTH -> y = 2;
            case LONG_CARDINAL_EAST -> x = 1;
            case LONG_CARDINAL_WEST -> x = 3;
            default -> {
            }
        }

        moveRoverBusiness.move(marsRoverModelSpy, MOVE_BACKWARD);
        verify(marsRoverDao, atMostOnce()).updateCoordinate(anyString(),anyInt(), anyInt());
        verify(marsRoverDao, atMostOnce()).findByRoverName(any());
        assertEquals(x, positionModelSpy.getXCoordinate());
        assertEquals(y, positionModelSpy.getYCoordinate());
    }

    @Test
    public void testMoveInvalidCardinalDirection() {
        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal("CARDINAL_UNKNOWN");
        marsRoverModelSpy.setPosition(positionModelSpy);

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(exceptionUtil).createInvalidCardinalPositionException(anyString());

        try {
            moveRoverBusiness.move(marsRoverModelSpy, MOVE_FORWARD);
            fail("Exception Caught");
        } catch (ResponseStatusException rse) {
            verify(marsRoverDao, never()).updateCoordinate(anyString(), anyInt(), anyInt());
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
        }
    }

    @Test
    public void testMoveInvalidMoveCommand() {
        MarsRoverModel marsRoverModelMock = mock(MarsRoverModel.class);
        PositionModel positionModelMock = mock(PositionModel.class);
        when(marsRoverModelMock.getPosition()).thenReturn(positionModelMock);
        when(positionModelMock.getCardinal()).thenReturn(StringUtils.EMPTY);

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(exceptionUtil).createInvalidMovementException(anyString());

        try {
            moveRoverBusiness.move(marsRoverModelMock, "MOVE_UNKNOWN");
            fail("Exception Caught");
        } catch (ResponseStatusException rse) {
            verify(marsRoverDao, never()).updateCoordinate(anyString(), anyInt(), anyInt());
            assertEquals(HttpStatus.BAD_REQUEST, rse.getStatusCode());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveForwardCollisionDetected(String input) {
        int x = 1;
        int y = 2;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(positionRoverBusiness.determineCollisionOtherWithRoverName(any())).thenReturn(true);

        try {
            moveRoverBusiness.move(marsRoverModelSpy, MOVE_FORWARD);
            fail("Exception Caught!");
        }catch(RuntimeException runtimeException){
            verify(marsRoverDao, never()).updateCoordinate(anyString(),anyInt(), anyInt());
            verify(marsRoverDao, atMostOnce()).findByRoverName(any());
            assertEquals(x, positionModelSpy.getXCoordinate());
            assertEquals(y, positionModelSpy.getYCoordinate());
            assertEquals("Collision Detected", runtimeException.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testMoveBackwardCollisionDetected(String input) {
        int x = 2;
        int y = 1;

        MarsRoverModel marsRoverModelSpy = spy(MarsRoverModel.class);
        PositionModel positionModelSpy = spy(PositionModel.class);
        positionModelSpy.setCardinal(input);
        positionModelSpy.setXCoordinate(x);
        positionModelSpy.setYCoordinate(y);
        marsRoverModelSpy.setPosition(positionModelSpy);

        when(positionRoverBusiness.determineCollisionOtherWithRoverName(any())).thenReturn(true);

        try {
            moveRoverBusiness.move(marsRoverModelSpy, MOVE_BACKWARD);
            fail("Exception Caught!");
        }catch(RuntimeException runtimeException){
            verify(marsRoverDao, never()).updateCoordinate(anyString(),anyInt(), anyInt());
            verify(marsRoverDao, atMostOnce()).findByRoverName(any());
            assertEquals(x, positionModelSpy.getXCoordinate());
            assertEquals(y, positionModelSpy.getYCoordinate());
            assertEquals("Collision Detected", runtimeException.getMessage());
        }
    }
}