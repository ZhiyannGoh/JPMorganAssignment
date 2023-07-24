package com.jpmorgan.zhiyan.assignment.unit;

import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;

import static com.jpmorgan.zhiyan.assignment.model.PositionModel.SHORT_CARDINAL_E;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {
    @BeforeEach
    protected void init() {
        MockitoAnnotations.openMocks(this);
    }

    protected static MarsRoverModel getSampleMarsRoverModelMock(){
        String roverName = "SOME_NAME";
        int x=0;
        int y=0;
        MarsRoverModel marsRoverModelMock = mock(MarsRoverModel.class);
        PositionModel positionModelMock = mock(PositionModel.class);
        when(marsRoverModelMock.getRoverName()).thenReturn(roverName);
        when(marsRoverModelMock.getPosition()).thenReturn(positionModelMock);
        when(positionModelMock.getXCoordinate()).thenReturn(x);
        when(positionModelMock.getYCoordinate()).thenReturn(y);
        when(positionModelMock.getCardinal()).thenReturn(SHORT_CARDINAL_E);
        return marsRoverModelMock;
    }
}