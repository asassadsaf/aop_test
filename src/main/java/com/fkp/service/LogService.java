package com.fkp.service;


import com.fkp.domain.OperationLog;
import com.fkp.param.BaseResponse;

public interface LogService {
    BaseResponse<?> saveOperationLog(OperationLog operationLog);
}
