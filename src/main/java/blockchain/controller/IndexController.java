package blockchain.controller;


import blockchain.dto.response.ResultInfo;
import blockchain.enums.ResponseCodeEnum;
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
