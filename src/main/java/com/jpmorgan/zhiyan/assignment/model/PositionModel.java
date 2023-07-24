package com.jpmorgan.zhiyan.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class PositionModel {
    public static final String MOVE_FORWARD = "f";

    public static final String MOVE_BACKWARD = "b";

    public static final String ROTATE_RIGHT = "r";

    public static final String ROTATE_LEFT = "l";

    public static final String SHORT_CARDINAL_N = "N";

    public static final String SHORT_CARDINAL_S = "S";

    public static final String SHORT_CARDINAL_E = "E";

    public static final String SHORT_CARDINAL_W = "W";

    public static final String LONG_CARDINAL_NORTH = "North";

    public static final String LONG_CARDINAL_SOUTH = "South";

    public static final String LONG_CARDINAL_EAST = "East";

    public static final String LONG_CARDINAL_WEST = "West";

    protected static final Map<String, String> cardinalMap = new HashMap<>();

    static {
        cardinalMap.put(SHORT_CARDINAL_N, LONG_CARDINAL_NORTH);
        cardinalMap.put(SHORT_CARDINAL_S, LONG_CARDINAL_SOUTH);
        cardinalMap.put(SHORT_CARDINAL_E, LONG_CARDINAL_EAST);
        cardinalMap.put(SHORT_CARDINAL_W, LONG_CARDINAL_WEST);
    }

    public static final Map<String, String> immutableCardinalMap = Collections.unmodifiableMap(new LinkedHashMap<>(cardinalMap));

    @Column(name = "x_axis", nullable = false)
    @JsonProperty("X-Axis")
    private int xCoordinate;

    @Column(name = "y_axis", nullable = false)
    @JsonProperty("Y-Axis")
    private int yCoordinate;

    @NonNull
    @Column(name = "cardinal_direction", nullable = false)
    @JsonProperty("Cardinal Direction")
    private String cardinal;

    public String getLongCardinal(String shortCardinal) {
        return cardinalMap.get(shortCardinal);
    }
}