package com.jpmorgan.zhiyan.assignment.unit.handler;

import com.jpmorgan.zhiyan.assignment.business.MoveRoverBusiness;
import com.jpmorgan.zhiyan.assignment.business.PositionRoverBusiness;
import com.jpmorgan.zhiyan.assignment.handler.MoveRoverHandler;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.model.request.MoveRoverRequestModel;
import com.jpmorgan.zhiyan.assignment.model.response.MoveRoverResponseModel;
import com.jpmorgan.zhiyan.assignment.unit.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MoveRoverHandlerTest extends BaseTest {
    @InjectMocks
    private MoveRoverHandler moveRoverHandler;

    @Mock
    private MoveRoverBusiness moveRoverBusiness;

    @Mock
    private PositionRoverBusiness positionRoverBusiness;

    @Test
    public void testMoveRoverInvalidCommand() {
        String command = "u";
        MoveRoverRequestModel moveRoverRequestModelMock = mock(MoveRoverRequestModel.class);
        when(moveRoverRequestModelMock.getCommands()).thenReturn(command);

        MarsRoverModel marsRoverModelMock = getSampleMarsRoverModelMock();
        when(positionRoverBusiness.getRoverPosition(any())).thenReturn(marsRoverModelMock);

        MoveRoverResponseModel moveRoverResponseModel = moveRoverHandler.moveRover(moveRoverRequestModelMock);
        PositionModel positionModel = moveRoverResponseModel.getPosition();

        assertEquals(marsRoverModelMock.getRoverName(), moveRoverResponseModel.getName());
        assertEquals(marsRoverModelMock.getPosition().getXCoordinate(), positionModel.getXCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getYCoordinate(), positionModel.getYCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getCardinal(), positionModel.getCardinal());
        assertTrue(moveRoverResponseModel.getCommandExecuted().isEmpty());
        assertTrue(moveRoverResponseModel.getCollisionDetected().isEmpty());
        assertEquals(List.of(command).toString(), moveRoverResponseModel.getRequestCommand());
        verify(moveRoverBusiness, never()).move(any(), any());
        verify(moveRoverBusiness, never()).rotate(any(), any());
    }

    @Test
    public void testMoveRoverValidCommand() {
        String command = "l,f";
        MoveRoverRequestModel moveRoverRequestModelMock = mock(MoveRoverRequestModel.class);
        when(moveRoverRequestModelMock.getCommands()).thenReturn(command);

        MarsRoverModel marsRoverModelMock = getSampleMarsRoverModelMock();
        when(positionRoverBusiness.getRoverPosition(any())).thenReturn(marsRoverModelMock);

        MoveRoverResponseModel moveRoverResponseModel = moveRoverHandler.moveRover(moveRoverRequestModelMock);
        PositionModel positionModel = moveRoverResponseModel.getPosition();

        assertEquals(marsRoverModelMock.getRoverName(), moveRoverResponseModel.getName());
        assertEquals(marsRoverModelMock.getPosition().getXCoordinate(), positionModel.getXCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getYCoordinate(), positionModel.getYCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getCardinal(), positionModel.getCardinal());
        assertEquals("lf", moveRoverResponseModel.getCommandExecuted());
        assertTrue(moveRoverResponseModel.getCollisionDetected().isEmpty());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens(command, ",")).toString(), moveRoverResponseModel.getRequestCommand());
        verify(moveRoverBusiness, atMost(2)).move(any(), any());
        verify(moveRoverBusiness, atMost(2)).rotate(any(), any());
    }

    @Test
    public void testMoveRoverValidCommandWithCollision() {
        String command = "l,f,f,b";
        MoveRoverRequestModel moveRoverRequestModelMock = mock(MoveRoverRequestModel.class);
        when(moveRoverRequestModelMock.getCommands()).thenReturn(command);

        MarsRoverModel marsRoverModelMock = getSampleMarsRoverModelMock();
        when(positionRoverBusiness.getRoverPosition(any())).thenReturn(marsRoverModelMock);
        doNothing().doThrow(new RuntimeException()).when(moveRoverBusiness).move(any(), any());

        MoveRoverResponseModel moveRoverResponseModel = moveRoverHandler.moveRover(moveRoverRequestModelMock);
        PositionModel positionModel = moveRoverResponseModel.getPosition();
        assertEquals(marsRoverModelMock.getRoverName(), moveRoverResponseModel.getName());
        assertEquals(marsRoverModelMock.getPosition().getXCoordinate(), positionModel.getXCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getYCoordinate(), positionModel.getYCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getCardinal(), positionModel.getCardinal());
        assertEquals("lff", moveRoverResponseModel.getCommandExecuted());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens("b", ",")).toString(), moveRoverResponseModel.getCollisionDetected());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens(command, ",")).toString(), moveRoverResponseModel.getRequestCommand());
        verify(moveRoverBusiness, times(2)).move(any(), any());
        verify(moveRoverBusiness, times(1)).rotate(any(), any());
    }
}
