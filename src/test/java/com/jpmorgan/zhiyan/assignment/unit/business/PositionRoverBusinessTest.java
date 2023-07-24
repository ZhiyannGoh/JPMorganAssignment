package com.jpmorgan.zhiyan.assignment.unit.business;

import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.unit.BaseTest;
import com.jpmorgan.zhiyan.assignment.business.PositionRoverBusiness;
import com.jpmorgan.zhiyan.assignment.dao.MarsRoverDao;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.util.ExceptionUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PositionRoverBusinessTest extends BaseTest {
    @InjectMocks
    private PositionRoverBusiness positionRoverBusiness;

    @Mock
    private MarsRoverDao marsRoverDao;

    @Mock
    private ExceptionUtil exceptionUtil;

    @Test
    public void testGetRoverPosition() {
        List<MarsRoverModel> roverListMock = mock(ArrayList.class);
        when(marsRoverDao.findByRoverName(anyString())).thenReturn(roverListMock);

        when(roverListMock.size()).thenReturn(1);

        positionRoverBusiness.getRoverPosition(anyString());
        verify(exceptionUtil, never()).createRoverNotFoundException(any());
    }

    @Test
    public void testGetRoverPositionRoverNameNotFound() {
        String roverName = "UNKNOWN";
        List<MarsRoverModel> roverListMock = mock(ArrayList.class);
        when(marsRoverDao.findByRoverName(anyString())).thenReturn(roverListMock);

        when(roverListMock.size()).thenReturn(0);
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(exceptionUtil).createRoverNotFoundException(any());

        try {
            positionRoverBusiness.getRoverPosition(roverName);
            fail("Exception caught!");
        } catch (ResponseStatusException rse) {
            verify(exceptionUtil, atMostOnce()).createRoverNotFoundException(any());
            assertEquals(HttpStatus.NOT_FOUND, rse.getStatusCode());
        }
    }

    @Test
    public void testDetermineCollisionIsDetected() {
        //To set up Rover to be created/placed
        MarsRoverModel creatingRoverMock = mock(MarsRoverModel.class);
        PositionModel creatingPositionMock = mock(PositionModel.class);
        when(creatingRoverMock.getPosition()).thenReturn(creatingPositionMock);
        when(creatingPositionMock.getXCoordinate()).thenReturn(1);
        when(creatingPositionMock.getYCoordinate()).thenReturn(2);

        // To set up Collision Set (Rover)
        List<MarsRoverModel> roverListSpy = spy(ArrayList.class);
        MarsRoverModel existingRover1Mock = mock(MarsRoverModel.class);
        roverListSpy.add(existingRover1Mock);
        MarsRoverModel existingRover2Mock = mock(MarsRoverModel.class);
        roverListSpy.add(existingRover2Mock);
        assertEquals(2, roverListSpy.size());

        // To set up Collision Set (Position)
        PositionModel existingPositionModel = mock(PositionModel.class);
        when(existingRover1Mock.getPosition()).thenReturn(existingPositionModel);
        when(existingRover2Mock.getPosition()).thenReturn(existingPositionModel);

        when(existingPositionModel.getXCoordinate()).thenReturn(1).thenReturn(2);
        when(existingPositionModel.getYCoordinate()).thenReturn(2).thenReturn(1);

        when(marsRoverDao.findAll()).thenReturn(roverListSpy);

        assertTrue(positionRoverBusiness.determineCollision(creatingRoverMock));
        verify(marsRoverDao, atMostOnce()).findAll();
    }

    @Test
    public void testDetermineCollisionNotDetected() {
        //To set up Rover to be created/placed
        MarsRoverModel creatingRoverMock = mock(MarsRoverModel.class);
        PositionModel creatingPositionMock = mock(PositionModel.class);
        when(creatingRoverMock.getPosition()).thenReturn(creatingPositionMock);
        when(creatingPositionMock.getXCoordinate()).thenReturn(1);
        when(creatingPositionMock.getYCoordinate()).thenReturn(1);

        // To set up Collision Set (Rover)
        List<MarsRoverModel> roverListSpy = spy(ArrayList.class);
        MarsRoverModel existingRover1Mock = mock(MarsRoverModel.class);
        roverListSpy.add(existingRover1Mock);
        MarsRoverModel existingRover2Mock = mock(MarsRoverModel.class);
        roverListSpy.add(existingRover2Mock);
        assertEquals(2, roverListSpy.size());

        // To set up Collision Set (Position)
        PositionModel existingPositionModel = mock(PositionModel.class);
        when(existingRover1Mock.getPosition()).thenReturn(existingPositionModel);
        when(existingRover2Mock.getPosition()).thenReturn(existingPositionModel);

        when(existingPositionModel.getXCoordinate()).thenReturn(1).thenReturn(2);
        when(existingPositionModel.getYCoordinate()).thenReturn(2).thenReturn(1);

        when(marsRoverDao.findAll()).thenReturn(roverListSpy);

        assertFalse(positionRoverBusiness.determineCollision(creatingRoverMock));
        verify(marsRoverDao, atMostOnce()).findAll();
    }
}