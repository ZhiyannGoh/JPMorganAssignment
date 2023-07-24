package com.jpmorgan.zhiyan.assignment.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmorgan.zhiyan.assignment.MarsRoverApplication;
import com.jpmorgan.zhiyan.assignment.dao.MarsRoverDao;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.model.request.CreateRoverRequestModel;
import com.jpmorgan.zhiyan.assignment.model.response.CreateRoverResponseModel;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_EAST;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.SHORT_CARDINAL_E;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.immutableCardinalMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = MarsRoverApplication.class)
@AutoConfigureMockMvc
@Transactional
public class CreateRoverApiFT extends BaseFT {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarsRoverDao marsRoverDao;

    private String createPath;

    private final int x = 1;
    private final int y = 1;

    @BeforeAll
    public void init() {
        createPath = this.getUri(CREATE_ROVER_PATH);
    }

    @Test
    public void testCreateRoverApiCreateSuccessWithSystemGeneratedName() throws Exception {
        CreateRoverRequestModel createRoverRequestModel = CreateRoverRequestModel.builder()
                .positionModel(PositionModel.builder()
                        .xCoordinate(x)
                        .yCoordinate(y)
                        .cardinal(SHORT_CARDINAL_E)
                        .build())
                .build();
        String requestJson = objectMapper.writeValueAsString(createRoverRequestModel);

        MarsRoverModel marsRoverModel =  MarsRoverModel.builder()
                .id(1)
                .position(PositionModel.builder()
                        .xCoordinate(x)
                        .yCoordinate(y)
                        .cardinal(LONG_CARDINAL_EAST)
                        .build())
                .build();
        when(marsRoverDao.findByRoverName(any())).thenReturn(Collections.singletonList(marsRoverModel));

        MvcResult result = mockMvc.perform(post(createPath).contentType("application/json").content(requestJson))
                .andExpect(status().isOk()).andReturn();
        String responseString = result.getResponse().getContentAsString();

        CreateRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, CreateRoverResponseModel.class);
        PositionModel responsePositionModel = responseRoverModel.getPosition();
        assertEquals("R1", responseRoverModel.getName());
        assertEquals(x, responsePositionModel.getXCoordinate());
        assertEquals(y, responsePositionModel.getYCoordinate());
        assertEquals(immutableCardinalMap.get(SHORT_CARDINAL_E), responsePositionModel.getCardinal());
    }

    @Test
    public void testCreateRoverApiCreateSuccessWithCustomizedName() throws Exception {
        CreateRoverRequestModel createRoverRequestModel = CreateRoverRequestModel.builder()
                .name("CustomizedRover")
                .positionModel(PositionModel.builder()
                        .xCoordinate(x)
                        .yCoordinate(y)
                        .cardinal(SHORT_CARDINAL_E)
                        .build())
                .build();
        String requestJson = objectMapper.writeValueAsString(createRoverRequestModel);

        when(marsRoverDao.findByRoverName(any())).thenReturn(Collections.emptyList());

        MvcResult result = mockMvc.perform(post(createPath).contentType("application/json").content(requestJson))
                .andExpect(status().isOk()).andReturn();
        String responseString = result.getResponse().getContentAsString();

        CreateRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, CreateRoverResponseModel.class);
        PositionModel responsePositionModel = responseRoverModel.getPosition();
        assertEquals("CustomizedRover", responseRoverModel.getName());
        assertEquals(x, responsePositionModel.getXCoordinate());
        assertEquals(y, responsePositionModel.getYCoordinate());
        assertEquals(immutableCardinalMap.get(SHORT_CARDINAL_E), responsePositionModel.getCardinal());
    }

    @Test
    public void testCreateRoverApiCreateFail() throws Exception {
        CreateRoverRequestModel createRoverRequestModel = CreateRoverRequestModel.builder()
                .positionModel(PositionModel.builder()
                        .xCoordinate(x)
                        .yCoordinate(y)
                        .cardinal(SHORT_CARDINAL_E)
                        .build())
                .build();
        String requestJson = objectMapper.writeValueAsString(createRoverRequestModel);

        MarsRoverModel marsRoverModel = MarsRoverModel.builder()
                .roverName("R1")
                .position(PositionModel.builder()
                        .xCoordinate(x)
                        .yCoordinate(y)
                        .cardinal(SHORT_CARDINAL_E)
                        .build())
                .build();
        when(marsRoverDao.findAll()).thenReturn(Collections.singletonList(marsRoverModel));

        MvcResult result = mockMvc.perform(post(createPath).contentType("application/json").content(requestJson))
                .andExpect(status().isBadRequest()).andReturn();
        assertEquals("Collision Detected. Rover is not created", result.getResponse().getErrorMessage());
    }
}