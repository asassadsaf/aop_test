package com.fkp.controller;

import com.fkp.entity.User;
import com.fkp.exception.BusinessException;
import com.fkp.param.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/targetTest", produces = MediaType.APPLICATION_JSON_VALUE)
public class TargetTestController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public BaseResponse<User> getUserById(@RequestParam(value = "id", required = false) String id){
        if("000".equals(id)){
            throw new RuntimeException("user id not be 000");
        }
        if(!StringUtils.isNumeric(id)){
            throw new BusinessException("999977", "用户id必须为非负整数!");
        }
        User user = new User(id, "张三", 24);
        return BaseResponse.success(user);
    }

}
