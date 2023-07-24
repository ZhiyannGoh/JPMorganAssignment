package com.jpmorgan.zhiyan.assignment.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmorgan.zhiyan.assignment.MarsRoverApplication;
import com.jpmorgan.zhiyan.assignment.dao.MarsRoverDao;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.model.response.PositionRoverResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_EAST;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_NORTH;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_SOUTH;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_WEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = MarsRoverApplication.class)
@AutoConfigureMockMvc
public class PositionRoverApiFT extends BaseFT {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarsRoverDao marsRoverDao;

    private String positionPath;

    @BeforeAll
    public void init() {
        positionPath = this.getUri(ROVER_POSITION_PATH);
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_NORTH, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_WEST})
    public void testRoverPositionApiValidateCardinalSuccess(String input) throws Exception {
        MarsRoverModel rover = MarsRoverModel.builder().roverName("R0")
                .position(PositionModel.builder()
                        .xCoordinate(1)
                        .yCoordinate(2)
                        .cardinal(input)
                        .build())
                .build();
        List<MarsRoverModel> roverList = Collections.singletonList(rover);
        when(marsRoverDao.findByRoverName(anyString())).thenReturn(roverList);

        MvcResult result = mockMvc.perform(get(positionPath, "R0").contentType("application/json"))
                .andExpect(status().isOk()).andReturn();

        String responseString = result.getResponse().getContentAsString();

        PositionRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, PositionRoverResponseModel.class);

        PositionModel responsePositionModel = responseRoverModel.getPosition();
        assertEquals("R0", responseRoverModel.getName());
        assertEquals(1, responsePositionModel.getXCoordinate());
        assertEquals(2, responsePositionModel.getYCoordinate());
        assertEquals(input, responsePositionModel.getCardinal());
    }

    @ParameterizedTest
    @ValueSource(strings = {"R1","R2"})
    public void testRoverPositionApiValidateRoverNameSuccess(String input) throws Exception {
        MarsRoverModel rover = MarsRoverModel.builder().roverName(input)
                .position(PositionModel.builder()
                        .xCoordinate(1)
                        .yCoordinate(2)
                        .cardinal(LONG_CARDINAL_NORTH)
                        .build())
                .build();
        List<MarsRoverModel> roverList = Collections.singletonList(rover);
        when(marsRoverDao.findByRoverName(anyString())).thenReturn(roverList);

        MvcResult result = mockMvc.perform(get(positionPath, "R0").contentType("application/json"))
                .andExpect(status().isOk()).andReturn();

        String responseString = result.getResponse().getContentAsString();

        PositionRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, PositionRoverResponseModel.class);

        PositionModel responsePositionModel = responseRoverModel.getPosition();
        assertEquals(input, responseRoverModel.getName());
        assertEquals(1, responsePositionModel.getXCoordinate());
        assertEquals(2, responsePositionModel.getYCoordinate());
        assertEquals(LONG_CARDINAL_NORTH, responsePositionModel.getCardinal());
    }

    @ParameterizedTest
    @CsvSource({
            "1,2",
            "2,2",
            "2,1"
    })
    public void testRoverPositionApiValidateCoordinateSuccess(String xStr, String yStr) throws Exception {
        int xAxis = Integer.parseInt(xStr);
        int yAxis = Integer.parseInt(yStr);

        MarsRoverModel rover = MarsRoverModel.builder().roverName("R0")
                .position(PositionModel.builder()
                        .xCoordinate(xAxis)
                        .yCoordinate(yAxis)
                        .cardinal(LONG_CARDINAL_NORTH)
                        .build())
                .build();
        List<MarsRoverModel> roverList = Collections.singletonList(rover);
        when(marsRoverDao.findByRoverName(anyString())).thenReturn(roverList);

        MvcResult result = mockMvc.perform(get(positionPath, "R0").contentType("application/json"))
                .andExpect(status().isOk()).andReturn();
        String responseString = result.getResponse().getContentAsString();

        PositionRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, PositionRoverResponseModel.class);

        PositionModel responsePositionModel = responseRoverModel.getPosition();
        assertEquals("R0", responseRoverModel.getName());
        assertEquals(xAxis, responsePositionModel.getXCoordinate());
        assertEquals(yAxis, responsePositionModel.getYCoordinate());
        assertEquals(LONG_CARDINAL_NORTH, responsePositionModel.getCardinal());
    }

    @Test
    public void testRoverPositionApiFail() throws Exception {
        String roverName = "UNKNOWN";
        MvcResult result = mockMvc.perform(get(positionPath, roverName).contentType("application/json"))
                .andExpect(status().isNotFound()).andReturn();
        assertEquals("Rover " + roverName + " not found", result.getResponse().getErrorMessage());
    }
}