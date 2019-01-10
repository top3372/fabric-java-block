package com.ideal.blockchain.controller.block;

import com.ideal.blockchain.dto.request.UserDto;
import com.ideal.blockchain.dto.response.ResultInfo;
import com.ideal.blockchain.enums.ResponseCodeEnum;
import com.ideal.blockchain.service.block.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author: LeonMa
 * @date: 2019/01/10 12:02
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/enroll", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo enroll(@RequestBody UserDto user) {
        try {
            if (StringUtils.isEmpty(user.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
//            if (StringUtils.isEmpty(user.getPassWord())) {
//                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter password in request body");
//            }
            if (StringUtils.isEmpty(user.getPeerWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            String result = userService.register(user.getUserName(), user.getPassWord(), user.getPeerWithOrg());
            return new ResultInfo(ResponseCodeEnum.SUCCESS, result);

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }
}
