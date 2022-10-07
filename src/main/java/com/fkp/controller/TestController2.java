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
@RequestMapping(value = "/test2", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@LogPointCut
public class TestController2 {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public BaseResponse<User> getUserById(@NotBlank(message = "User ID not blank!") String id){
        User user = new User(id, "张三", 23);
        return BaseResponse.success(user);
    }
}
