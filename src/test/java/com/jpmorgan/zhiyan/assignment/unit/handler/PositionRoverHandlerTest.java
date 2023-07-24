package com.jpmorgan.zhiyan.assignment.unit.handler;

import com.jpmorgan.zhiyan.assignment.business.PositionRoverBusiness;
import com.jpmorgan.zhiyan.assignment.handler.PositionRoverHandler;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.model.response.PositionRoverResponseModel;
import com.jpmorgan.zhiyan.assignment.unit.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PositionRoverHandlerTest extends BaseTest {
    @InjectMocks
    private PositionRoverHandler positionRoverHandler;

    @Mock
    private PositionRoverBusiness positionRoverBusiness;

    @Test
    public void testGetRoverPositionSuccess() {
        MarsRoverModel marsRoverModelMock = getSampleMarsRoverModelMock();
        when(positionRoverBusiness.getRoverPosition(any())).thenReturn(marsRoverModelMock);

        PositionRoverResponseModel positionRoverResponseModel = positionRoverHandler.getRoverPosition(anyString());
        PositionModel positionModel = positionRoverResponseModel.getPosition();

        assertEquals(marsRoverModelMock.getRoverName(), positionRoverResponseModel.getName());
        assertEquals(marsRoverModelMock.getPosition().getXCoordinate(), positionModel.getXCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getYCoordinate(), positionModel.getYCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getCardinal(), positionModel.getCardinal());
        verify(positionRoverBusiness, atMostOnce()).getRoverPosition(any());
    }
}