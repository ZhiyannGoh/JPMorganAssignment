package com.jpmorgan.zhiyan.assignment.functional;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TestPropertySource(locations = "classpath:test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseFT {
    public static final String ROVER_POSITION_PATH = "rover/position?roverName={queryVal}";

    public static final String CREATE_ROVER_PATH = "rover/create";

    public static final String MOVE_ROVER_PATH = "rover/move";

    @Value("${hostname}")
    public String hostname;

    protected String getUri(String resourcePath) {
        return this.hostname + resourcePath;
    }
}