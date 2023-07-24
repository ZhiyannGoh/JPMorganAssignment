package com.jpmorgan.zhiyan.assignment.business;

import com.jpmorgan.zhiyan.assignment.dao.MarsRoverDao;
import com.jpmorgan.zhiyan.assignment.model.MarsRoverModel;
import com.jpmorgan.zhiyan.assignment.model.PositionModel;
import com.jpmorgan.zhiyan.assignment.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static java.lang.System.currentTimeMillis;


@Service
public class CreateRoverBusiness {
    @Autowired
    MarsRoverDao marsRoverDao;

    @Autowired
    ExceptionUtil exceptionUtil;

    public MarsRoverModel createRover(MarsRoverModel marsRoverModel) {
        if (StringUtils.isEmpty(marsRoverModel.getRoverName())) {
            return createRoverWithSystemGeneratorName(marsRoverModel.getPosition());
        }
        return createRoverWithCustomizedName(marsRoverModel);
    }

    private MarsRoverModel createRoverWithCustomizedName(MarsRoverModel marsRoverModel) {
        String roverName = marsRoverModel.getRoverName();
        PositionModel positionModel = marsRoverModel.getPosition();
        List<MarsRoverModel> createdMarsRoverModelList = marsRoverDao.findByRoverName(roverName);

        if (!createdMarsRoverModelList.isEmpty()) {
            exceptionUtil.createRoverNameAlreadyExistException(roverName);
        }

        marsRoverDao.insertRover(roverName, positionModel.getXCoordinate(), positionModel.getYCoordinate(), positionModel.getCardinal());
        return marsRoverModel;
    }

    private MarsRoverModel createRoverWithSystemGeneratorName(PositionModel positionModel) {
        Timestamp timestamp = new Timestamp(currentTimeMillis());
        String placeholderRoverName = timestamp + " " + positionModel.getXCoordinate() + positionModel.getYCoordinate() + positionModel.getCardinal();
        marsRoverDao.insertRover(placeholderRoverName, positionModel.getXCoordinate(), positionModel.getYCoordinate(), positionModel.getCardinal());
        List<MarsRoverModel> createdMarsRoverModelList = marsRoverDao.findByRoverName(placeholderRoverName);

        if (createdMarsRoverModelList.isEmpty()) {
            exceptionUtil.createDataNotFoundNeededException(placeholderRoverName);
        }

        if (createdMarsRoverModelList.size() > 1) {
            exceptionUtil.createDatabaseReconciliationNeededException(placeholderRoverName);
        }

        MarsRoverModel createdMarsRoverModel = createdMarsRoverModelList.get(0);
        createdMarsRoverModel.setRoverName("R" + createdMarsRoverModel.getId());
        marsRoverDao.updateRoverName(createdMarsRoverModel.getId(), createdMarsRoverModel.getRoverName());
        return createdMarsRoverModel;
    }
}