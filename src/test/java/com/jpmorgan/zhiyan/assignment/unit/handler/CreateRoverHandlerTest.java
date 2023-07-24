package com.jpmorgan.zhiyan.assignment.unit.handler;

import com.jpmorgan.zhiyan.assignment.business.CreateRoverBusiness;
import com.jpmorgan.zhiyan.assignment.business.PositionRoverBusiness;
import com.jpmorgan.zhiyan.assignment.handler.CreateRoverHandler;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.model.request.CreateRoverRequestModel;
import com.jpmorgan.zhiyan.assignment.model.response.CreateRoverResponseModel;
import com.jpmorgan.zhiyan.assignment.unit.BaseTest;
import com.jpmorgan.zhiyan.assignment.util.ExceptionUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateRoverHandlerTest extends BaseTest {
    @InjectMocks
    private CreateRoverHandler createRoverHandler;

    @Mock
    private PositionRoverBusiness positionRoverBusiness;

    @Mock
    private CreateRoverBusiness createRoverBusiness;

    @Mock
    private ExceptionUtil exceptionUtil;

    @Test
    public void testCreateRoverHandlerNoCollision() {
        CreateRoverRequestModel createRoverRequestModelMock = mock(CreateRoverRequestModel.class);
        PositionModel positionModelMock = mock(PositionModel.class);
        when(createRoverRequestModelMock.getPositionModel()).thenReturn(positionModelMock);

        MarsRoverModel marsRoverModelMock = getSampleMarsRoverModelMock();
        when(positionRoverBusiness.determineCollision(any())).thenReturn(false);
        when(createRoverBusiness.createRover(any())).thenReturn(marsRoverModelMock);

        CreateRoverResponseModel createRoverResponseModel = createRoverHandler.createRover(createRoverRequestModelMock);
        PositionModel positionModel = createRoverResponseModel.getPosition();

        assertEquals(marsRoverModelMock.getRoverName(), createRoverResponseModel.getName());
        assertEquals(marsRoverModelMock.getPosition().getXCoordinate(), positionModel.getXCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getYCoordinate(), positionModel.getYCoordinate());
        assertEquals(marsRoverModelMock.getPosition().getCardinal(), positionModel.getCardinal());
        verify(positionRoverBusiness, atMostOnce()).determineCollision(any());
        verify(createRoverBusiness, atMostOnce()).createRover(any());
        verify(exceptionUtil, never()).createCollisionDetectedDuringCreationException();
    }

    @Test
    public void testCreateRoverHandlerCollisionDetected() {
        CreateRoverRequestModel createRoverRequestModelMock = mock(CreateRoverRequestModel.class);
        PositionModel positionModelMock = mock(PositionModel.class);
        when(createRoverRequestModelMock.getPositionModel()).thenReturn(positionModelMock);

        when(positionRoverBusiness.determineCollision(any())).thenReturn(true);
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(exceptionUtil).createCollisionDetectedDuringCreationException();

        try {
            createRoverHandler.createRover(createRoverRequestModelMock);
            fail("Exception Caught!");
        } catch (ResponseStatusException rse) {
            verify(positionRoverBusiness, atMostOnce()).determineCollision(any());
            verify(createRoverBusiness, never()).createRover(any());
            verify(exceptionUtil, atMostOnce()).createCollisionDetectedDuringCreationException();
        }
    }
}