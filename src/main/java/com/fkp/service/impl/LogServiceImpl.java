package com.fkp.service.impl;

import com.fkp.constant.ErrorCodeEnum;
import com.fkp.domain.OperationLog;
import com.fkp.exception.BusinessException;
import com.fkp.mapper.OperationLogMapper;
import com.fkp.param.BaseResponse;
import com.fkp.service.LogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public BaseResponse<?> saveOperationLog(OperationLog operationLog) {
        operationLog = null;
        if(operationLog == null || StringUtils.isBlank(operationLog.getOperationCode())){
            return BaseResponse.fail(ErrorCodeEnum.OperationLogDataError.getCode());
        }
        int i = operationLogMapper.insertSelective(operationLog);
        if(i == 0){
            return BaseResponse.fail(ErrorCodeEnum.SaveOperationLogDataBaseError.getCode());
        }
        return BaseResponse.success();
    }
}
