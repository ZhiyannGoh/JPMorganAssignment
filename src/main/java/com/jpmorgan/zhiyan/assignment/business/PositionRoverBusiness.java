package com.jpmorgan.zhiyan.assignment.business;

import com.jpmorgan.zhiyan.assignment.dao.MarsRoverDao;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PositionRoverBusiness {
    @Autowired
    MarsRoverDao marsRoverDao;

    @Autowired
    ExceptionUtil exceptionUtil;

    public MarsRoverModel getRoverPosition(String roverName) {
        List<MarsRoverModel> roverList = marsRoverDao.findByRoverName(roverName);
        if (roverList.size() == 0) {
            exceptionUtil.createRoverNotFoundException(roverName);
        }
        return roverList.get(0);
    }

    public boolean determineCollision(MarsRoverModel toBeCreatedRover) {
        List<MarsRoverModel> roverModelList = marsRoverDao.findAll();
        Set<String> roverCollisionSet = getRoverCollisionSet(roverModelList);
        return isContainInCollisionSet(toBeCreatedRover, roverCollisionSet);
    }

    public boolean determineCollisionOtherWithRoverName(MarsRoverModel marsRoverModel) {
        List<MarsRoverModel> roverModelList = marsRoverDao.findOtherRovers(Collections.singletonList(marsRoverModel.getRoverName()));
        Set<String> roverCollisionSet = getRoverCollisionSet(roverModelList);
        return isContainInCollisionSet(marsRoverModel, roverCollisionSet);
    }

    private boolean isContainInCollisionSet(MarsRoverModel marsRoverModel, Set<String> roverCollisionSet){
        String toBeCreatedPosition = getPositionInStringFormat(marsRoverModel.getPosition());
        return roverCollisionSet.contains(toBeCreatedPosition);
    }

    private Set<String> getRoverCollisionSet(final List<MarsRoverModel> roverList) {
        Set<String> roverCollisionSet = new HashSet<>();

        roverList.forEach(currentRover -> {
            String position = getPositionInStringFormat(currentRover.getPosition());
            roverCollisionSet.add(position);
        });

        return roverCollisionSet;
    }

    public List<MarsRoverModel> findAllRovers() {
        return marsRoverDao.findAll();
    }

    private String getPositionInStringFormat(PositionModel positionModel) {
        List<String> currentPositionInformation = new ArrayList<>();
        currentPositionInformation.add(String.valueOf(positionModel.getXCoordinate()));
        currentPositionInformation.add(String.valueOf(positionModel.getYCoordinate()));
        return StringUtils.join(currentPositionInformation, ",");
    }
}