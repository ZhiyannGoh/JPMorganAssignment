package com.jpmorgan.zhiyan.assignment.unit.business;

import com.jpmorgan.zhiyan.assignment.business.CreateRoverBusiness;
import com.jpmorgan.zhiyan.assignment.dao.MarsRoverDao;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.unit.BaseTest;
import com.jpmorgan.zhiyan.assignment.util.ExceptionUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_EAST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateRoverBusinessTest extends BaseTest {
    @InjectMocks
    private CreateRoverBusiness createRoverBusiness;

    @Mock
    private MarsRoverDao marsRoverDao;

    @Mock
    private ExceptionUtil exceptionUtil;

    @Test
    public void testCreateRoverWithoutRoverName() {
        MarsRoverModel marsRoverModelMock = mock(MarsRoverModel.class);
        PositionModel positionModelMock = mock(PositionModel.class);
        when(marsRoverModelMock.getPosition()).thenReturn(positionModelMock);
        when(positionModelMock.getXCoordinate()).thenReturn(1);
        when(positionModelMock.getYCoordinate()).thenReturn(1);
        when(positionModelMock.getCardinal()).thenReturn(LONG_CARDINAL_EAST);

        //Set up database Rover record
        MarsRoverModel marsRoverModelDatabaseSpy = spy(MarsRoverModel.class);
        when(marsRoverModelDatabaseSpy.getId()).thenReturn(1L);
        when(marsRoverModelDatabaseSpy.getPosition()).thenReturn(positionModelMock);
        when(marsRoverDao.findByRoverName(any())).thenReturn(Collections.singletonList(marsRoverModelDatabaseSpy));

        createRoverBusiness.createRover(marsRoverModelMock);
        verify(marsRoverDao, atMostOnce()).insertRover(anyString(), anyInt(), anyInt(), anyString());
        verify(marsRoverDao, atMostOnce()).findByRoverName(anyString());
        verify(marsRoverDao, atMostOnce()).updateRoverName(anyLong(), anyString());
        assertEquals("R1", marsRoverModelDatabaseSpy.getRoverName());
    }

    @Test
    public void testCreateRoverWithoutRoverNameThrowEmptyQueryResult() {
        MarsRoverModel marsRoverModelMock = mock(MarsRoverModel.class);
        PositionModel positionModelMock = mock(PositionModel.class);
        when(marsRoverModelMock.getPosition()).thenReturn(positionModelMock);
        when(positionModelMock.getXCoordinate()).thenReturn(1);
        when(positionModelMock.getYCoordinate()).thenReturn(1);
        when(positionModelMock.getCardinal()).thenReturn(LONG_CARDINAL_EAST);

        when(marsRoverDao.findByRoverName(any())).thenReturn(Collections.emptyList());
        doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(exceptionUtil).createDataNotFoundNeededException(any());

        try {
            createRoverBusiness.createRover(marsRoverModelMock);
        } catch (ResponseStatusException rse) {
            verify(marsRoverDao, atMostOnce()).insertRover(anyString(), anyInt(), anyInt(), anyString());
            verify(marsRoverDao, atMostOnce()).findByRoverName(anyString());
            verify(marsRoverDao, never()).updateRoverName(anyLong(), anyString());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rse.getStatusCode());
        }
    }

    @Test
    public void testCreateRoverWithoutRoverNameThrowDoubleEntryQueryResult() {
        MarsRoverModel marsRoverModelMock = mock(MarsRoverModel.class);
        PositionModel positionModelMock = mock(PositionModel.class);
        when(marsRoverModelMock.getPosition()).thenReturn(positionModelMock);
        when(positionModelMock.getXCoordinate()).thenReturn(1);
        when(positionModelMock.getYCoordinate()).thenReturn(1);
        when(positionModelMock.getCardinal()).thenReturn(LONG_CARDINAL_EAST);

        List<MarsRoverModel> marsRoverModelList = mock(ArrayList.class);
        when(marsRoverDao.findByRoverName(any())).thenReturn(marsRoverModelList);
        when(marsRoverModelList.size()).thenReturn(2);
        doThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(exceptionUtil).createDatabaseReconciliationNeededException(any());

        try {
            createRoverBusiness.createRover(marsRoverModelMock);
        } catch (ResponseStatusException rse) {
            verify(marsRoverDao, atMostOnce()).insertRover(anyString(), anyInt(), anyInt(), anyString());
            verify(marsRoverDao, atMostOnce()).findByRoverName(anyString());
            verify(marsRoverDao, never()).updateRoverName(anyLong(), anyString());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rse.getStatusCode());
        }
    }
}