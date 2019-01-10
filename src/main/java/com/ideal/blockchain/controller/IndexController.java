package com.ideal.blockchain.controller;


import com.ideal.blockchain.dto.response.ResultInfo;
import com.ideal.blockchain.enums.ResponseCodeEnum;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {


    @RequestMapping(value = "/index")
    @ResponseBody
    public ResultInfo enroll() {
        return new ResultInfo(ResponseCodeEnum.SUCCESS, "SUCCESS");
    }


}
