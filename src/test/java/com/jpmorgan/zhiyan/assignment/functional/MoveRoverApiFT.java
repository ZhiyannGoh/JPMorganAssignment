package com.jpmorgan.zhiyan.assignment.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmorgan.zhiyan.assignment.MarsRoverApplication;
import com.jpmorgan.zhiyan.assignment.dao.MarsRoverDao;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.model.request.MoveRoverRequestModel;
import com.jpmorgan.zhiyan.assignment.model.response.MoveRoverResponseModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_EAST;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_NORTH;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_SOUTH;
import static com.jpmorgan.zhiyan.assignment.model.PositionModel.LONG_CARDINAL_WEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = MarsRoverApplication.class)
@AutoConfigureMockMvc
public class MoveRoverApiFT extends BaseFT {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarsRoverDao marsRoverDao;

    private String movePath;

    @BeforeAll
    public void init() {
        movePath = this.getUri(MOVE_ROVER_PATH);
    }

    @ParameterizedTest
    @ValueSource(strings = {"r", "r,r", "r,r,r", "r,r,r,r"})
    public void testMoveRoverRotateRightWithoutCollisionApiSuccess(String input) throws Exception {
        MoveRoverRequestModel moveRoverRequestModel = MoveRoverRequestModel.builder()
                .name("TEST")
                .commands(input)
                .build();
        String requestJson = objectMapper.writeValueAsString(moveRoverRequestModel);

        MarsRoverModel marsRoverModel = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(0)
                        .yCoordinate(0)
                        .cardinal(LONG_CARDINAL_NORTH)
                        .build())
                .build();
        when(marsRoverDao.findByRoverName(any())).thenReturn(Collections.singletonList(marsRoverModel));

        MvcResult result = mockMvc.perform(post(movePath).contentType("application/json").content(requestJson))
                .andExpect(status().isOk()).andReturn();
        String responseString = result.getResponse().getContentAsString();

        MoveRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, MoveRoverResponseModel.class);
        PositionModel positionModel = responseRoverModel.getPosition();

        assertEquals("TEST", responseRoverModel.getName());
        assertEquals(0, positionModel.getXCoordinate());
        assertEquals(0, positionModel.getYCoordinate());

        String cardinalDirection = StringUtils.EMPTY;
        String executedCommands = StringUtils.EMPTY;
        switch (input) {
            case "r" -> {
                cardinalDirection = LONG_CARDINAL_EAST;
                executedCommands = "r";
            }
            case "r,r" -> {
                cardinalDirection = LONG_CARDINAL_SOUTH;
                executedCommands = "rr";
            }
            case "r,r,r" -> {
                cardinalDirection = LONG_CARDINAL_WEST;
                executedCommands = "rrr";
            }
            case "r,r,r,r" -> {
                cardinalDirection = LONG_CARDINAL_NORTH;
                executedCommands = "rrrr";
            }
            default -> {
            }
        }

        assertEquals(cardinalDirection, positionModel.getCardinal());
        assertEquals(executedCommands, responseRoverModel.getCommandExecuted());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens(input, ",")).toString(), responseRoverModel.getRequestCommand());
    }

    @ParameterizedTest
    @ValueSource(strings = {"l", "l,l", "l,l,l", "l,l,l,l"})
    public void testMoveRoverRotateLeftWithoutCollisionApiSuccess(String input) throws Exception {
        MoveRoverRequestModel moveRoverRequestModel = MoveRoverRequestModel.builder()
                .name("TEST")
                .commands(input)
                .build();
        String requestJson = objectMapper.writeValueAsString(moveRoverRequestModel);

        MarsRoverModel marsRoverModel = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(0)
                        .yCoordinate(0)
                        .cardinal(LONG_CARDINAL_NORTH)
                        .build())
                .build();
        when(marsRoverDao.findByRoverName(any())).thenReturn(Collections.singletonList(marsRoverModel));

        MvcResult result = mockMvc.perform(post(movePath).contentType("application/json").content(requestJson))
                .andExpect(status().isOk()).andReturn();
        String responseString = result.getResponse().getContentAsString();

        MoveRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, MoveRoverResponseModel.class);
        PositionModel positionModel = responseRoverModel.getPosition();

        assertEquals("TEST", responseRoverModel.getName());
        assertEquals(0, positionModel.getXCoordinate());
        assertEquals(0, positionModel.getYCoordinate());

        String cardinalDirection = StringUtils.EMPTY;
        String executedCommands = StringUtils.EMPTY;
        switch (input) {
            case "l" -> {
                cardinalDirection = LONG_CARDINAL_WEST;
                executedCommands = "l";
            }
            case "l,l" -> {
                cardinalDirection = LONG_CARDINAL_SOUTH;
                executedCommands = "ll";
            }
            case "l,l,l" -> {
                cardinalDirection = LONG_CARDINAL_EAST;
                executedCommands = "lll";
            }
            case "l,l,l,l" -> {
                cardinalDirection = LONG_CARDINAL_NORTH;
                executedCommands = "llll";
            }
            default -> {
            }
        }

        assertEquals(cardinalDirection, positionModel.getCardinal());
        assertEquals(executedCommands, responseRoverModel.getCommandExecuted());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens(input, ",")).toString(), responseRoverModel.getRequestCommand());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_WEST, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_EAST})
    public void testMoveRoverForwardThriceButCollisionDetectedAfterFirstForward(String input) throws Exception {
        MoveRoverRequestModel moveRoverRequestModel = MoveRoverRequestModel.builder()
                .name("TEST")
                .commands("f,f,f")
                .build();
        String requestJson = objectMapper.writeValueAsString(moveRoverRequestModel);

        MarsRoverModel marsRoverModel = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(0)
                        .yCoordinate(0)
                        .cardinal(input)
                        .build())
                .build();
        when(marsRoverDao.findByRoverName(any())).thenReturn(Collections.singletonList(marsRoverModel));
        when(marsRoverDao.findOtherRovers(any())).thenReturn(getCollisionSetAfterMovingForwardOnceAtAllCardinalDirection());


        MvcResult result = mockMvc.perform(post(movePath).contentType("application/json").content(requestJson))
                .andExpect(status().isOk()).andReturn();
        String responseString = result.getResponse().getContentAsString();

        MoveRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, MoveRoverResponseModel.class);
        PositionModel positionModel = responseRoverModel.getPosition();

        assertEquals("TEST", responseRoverModel.getName());

        int x = 0;
        int y = 0;
        switch (input) {
            case LONG_CARDINAL_NORTH -> {
                x = 0;
                y = 1;
            }
            case LONG_CARDINAL_SOUTH -> {
                x = 0;
                y = -1;
            }
            case LONG_CARDINAL_EAST -> {
                x = 1;
                y = 0;
            }
            case LONG_CARDINAL_WEST -> {
                x = -1;
                y = 0;
            }
            default -> {
            }
        }

        assertEquals(x, positionModel.getXCoordinate());
        assertEquals(y, positionModel.getYCoordinate());
        assertEquals(input, positionModel.getCardinal());
        assertEquals("ff", responseRoverModel.getCommandExecuted());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens("f", ",")).toString(), responseRoverModel.getCollisionDetected());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens("f,f,f", ",")).toString(), responseRoverModel.getRequestCommand());
    }

    @ParameterizedTest
    @ValueSource(strings = {LONG_CARDINAL_WEST, LONG_CARDINAL_SOUTH, LONG_CARDINAL_EAST, LONG_CARDINAL_EAST})
    public void testMoveRoverBackwardThriceButCollisionDetectedAfterFirstBackward(String input) throws Exception {
        MoveRoverRequestModel moveRoverRequestModel = MoveRoverRequestModel.builder()
                .name("TEST")
                .commands("b,b,b")
                .build();
        String requestJson = objectMapper.writeValueAsString(moveRoverRequestModel);

        MarsRoverModel marsRoverModel = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(0)
                        .yCoordinate(0)
                        .cardinal(input)
                        .build())
                .build();
        when(marsRoverDao.findByRoverName(any())).thenReturn(Collections.singletonList(marsRoverModel));
        when(marsRoverDao.findOtherRovers(any())).thenReturn(getCollisionSetAfterMovingBackwardOnceAtAllCardinalDirection());


        MvcResult result = mockMvc.perform(post(movePath).contentType("application/json").content(requestJson))
                .andExpect(status().isOk()).andReturn();
        String responseString = result.getResponse().getContentAsString();

        MoveRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, MoveRoverResponseModel.class);
        PositionModel positionModel = responseRoverModel.getPosition();

        assertEquals("TEST", responseRoverModel.getName());

        int x = 0;
        int y = 0;
        switch (input) {
            case LONG_CARDINAL_NORTH -> {
                x = 0;
                y = -1;
            }
            case LONG_CARDINAL_SOUTH -> {
                x = 0;
                y = 1;
            }
            case LONG_CARDINAL_EAST -> {
                x = -1;
                y = 0;
            }
            case LONG_CARDINAL_WEST -> {
                x = 1;
                y = 0;
            }
            default -> {
            }
        }

        assertEquals(x, positionModel.getXCoordinate());
        assertEquals(y, positionModel.getYCoordinate());
        assertEquals(input, positionModel.getCardinal());
        assertEquals("bb", responseRoverModel.getCommandExecuted());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens("b", ",")).toString(), responseRoverModel.getCollisionDetected());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens("b,b,b", ",")).toString(), responseRoverModel.getRequestCommand());
    }

    @Test
    public void testMoveRoverFromXEquals3AndYEqual4AndNorthToXEquals5AndYEqual6AndEast() throws Exception {
        MoveRoverRequestModel moveRoverRequestModel = MoveRoverRequestModel.builder()
                .name("TEST")
                .commands("f,f,r,f,f")
                .build();
        String requestJson = objectMapper.writeValueAsString(moveRoverRequestModel);

        MarsRoverModel marsRoverModel = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(3)
                        .yCoordinate(4)
                        .cardinal(LONG_CARDINAL_NORTH)
                        .build())
                .build();
        when(marsRoverDao.findByRoverName(any())).thenReturn(Collections.singletonList(marsRoverModel));

        MvcResult result = mockMvc.perform(post(movePath).contentType("application/json").content(requestJson))
                .andExpect(status().isOk()).andReturn();
        String responseString = result.getResponse().getContentAsString();

        MoveRoverResponseModel responseRoverModel = objectMapper.readValue(responseString, MoveRoverResponseModel.class);
        PositionModel positionModel = responseRoverModel.getPosition();

        assertEquals("TEST", responseRoverModel.getName());
        assertEquals(5, positionModel.getXCoordinate());
        assertEquals(6, positionModel.getYCoordinate());
        assertEquals(LONG_CARDINAL_EAST, positionModel.getCardinal());
        assertEquals("ffrff", responseRoverModel.getCommandExecuted());
        assertEquals(Arrays.asList(StringUtils.splitPreserveAllTokens("f,f,r,f,f", ",")).toString(), responseRoverModel.getRequestCommand());
    }

    private List<MarsRoverModel> getCollisionSetAfterMovingForwardOnceAtAllCardinalDirection() {
        //Set up Collision Set
        MarsRoverModel marsRoverModel1 = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(0)
                        .yCoordinate(2)
                        .cardinal(LONG_CARDINAL_NORTH)
                        .build())
                .build();
        MarsRoverModel marsRoverModel2 = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(0)
                        .yCoordinate(-2)
                        .cardinal(LONG_CARDINAL_SOUTH)
                        .build())
                .build();
        MarsRoverModel marsRoverModel3 = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(-2)
                        .yCoordinate(0)
                        .cardinal(LONG_CARDINAL_WEST)
                        .build())
                .build();
        MarsRoverModel marsRoverModel4 = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(2)
                        .yCoordinate(0)
                        .cardinal(LONG_CARDINAL_EAST)
                        .build())
                .build();
        return List.of(marsRoverModel1, marsRoverModel2, marsRoverModel3, marsRoverModel4);
    }

    private List<MarsRoverModel> getCollisionSetAfterMovingBackwardOnceAtAllCardinalDirection() {
        //Set up Collision Set
        MarsRoverModel marsRoverModel1 = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(0)
                        .yCoordinate(-2)
                        .cardinal(LONG_CARDINAL_NORTH)
                        .build())
                .build();
        MarsRoverModel marsRoverModel2 = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(0)
                        .yCoordinate(2)
                        .cardinal(LONG_CARDINAL_SOUTH)
                        .build())
                .build();
        MarsRoverModel marsRoverModel3 = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(2)
                        .yCoordinate(0)
                        .cardinal(LONG_CARDINAL_WEST)
                        .build())
                .build();
        MarsRoverModel marsRoverModel4 = MarsRoverModel.builder()
                .roverName("TEST")
                .position(PositionModel.builder()
                        .xCoordinate(-2)
                        .yCoordinate(0)
                        .cardinal(LONG_CARDINAL_EAST)
                        .build())
                .build();
        return List.of(marsRoverModel1, marsRoverModel2, marsRoverModel3, marsRoverModel4);
    }
}
