package com.jpmorgan.zhiyan.assignment.unit.api;

import com.jpmorgan.zhiyan.assignment.api.CreateRoverApi;
import com.jpmorgan.zhiyan.assignment.api.MoveRoverApi;
import com.jpmorgan.zhiyan.assignment.api.PositionRoverApi;
import com.jpmorgan.zhiyan.assignment.handler.CreateRoverHandler;
import com.jpmorgan.zhiyan.assignment.handler.MoveRoverHandler;
import com.jpmorgan.zhiyan.assignment.handler.PositionRoverHandler;
import com.jpmorgan.zhiyan.assignment.unit.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

public class ApiTest extends BaseTest {

    @InjectMocks
    private CreateRoverApi createRoverApi;

    @Mock
    private CreateRoverHandler createRoverHandler;

    @InjectMocks
    private MoveRoverApi moveRoverApi;

    @Mock
    private MoveRoverHandler moveRoverHandler;

    @InjectMocks
    private PositionRoverApi positionRoverApi;

    @Mock
    private PositionRoverHandler positionRoverHandler;

    @Test
    public void testCreateRoverApi(){
        createRoverApi.createRover(any());
        verify(createRoverHandler, atMostOnce()).createRover(any());
    }

    @Test
    public void testRoverMovementApi(){
        moveRoverApi.moveRover(any());
        verify(moveRoverHandler, atMostOnce()).moveRover(any());
    }

    @Test
    public void TestCreateRoverApi(){
        positionRoverApi.getRoverPosition(any());
        verify(positionRoverHandler, atMostOnce()).getRoverPosition(any());
    }
}