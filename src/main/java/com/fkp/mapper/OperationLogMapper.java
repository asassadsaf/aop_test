package com.fkp.mapper;

import com.fkp.domain.OperationLog;
import com.fkp.domain.OperationLogExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: fkp
 * @time: 2022-09-29 23:37:38
 * @description:
 */
public interface OperationLogMapper {
    long countByExample(OperationLogExample example);

    int deleteByExample(OperationLogExample example);

    int insert(OperationLog row);

    int insertSelective(OperationLog row);

    List<OperationLog> selectByExample(OperationLogExample example);

    int updateByExampleSelective(@Param("row") OperationLog row, @Param("example") OperationLogExample example);

    int updateByExample(@Param("row") OperationLog row, @Param("example") OperationLogExample example);
}
