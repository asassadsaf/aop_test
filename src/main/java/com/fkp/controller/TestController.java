package com.fkp.controller;

import com.fkp.annotation.LogPointCut;
import com.fkp.entity.User;
import com.fkp.param.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TestController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @LogPointCut(operationCode = "GET_USER_BY_ID", operationDesc = "通过用户id查询用户信息")
    public BaseResponse<User> getUserById(@NotBlank(message = "User ID not blank!") String id){
        User user = new User(id, "张三", 23);
        return BaseResponse.success(user);
    }
}
