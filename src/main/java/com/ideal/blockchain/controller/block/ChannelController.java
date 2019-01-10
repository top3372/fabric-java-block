package com.ideal.blockchain.controller.block;

import com.ideal.blockchain.dto.request.ChannelDto;
import com.ideal.blockchain.dto.response.ResultInfo;
import com.ideal.blockchain.enums.ResponseCodeEnum;
import com.ideal.blockchain.service.block.ChannelService;
import com.ideal.blockchain.service.block.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author: LeonMa
 * @date: 2019/01/10 12:03
 */
@Slf4j
@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChannelService channelService;


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo createChannel(@RequestBody ChannelDto channelDto) {
        try {

            if (StringUtils.isEmpty(channelDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
//            if (StringUtils.isEmpty(channelDto.getPassWord())) {
//                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
//            }
            if (StringUtils.isEmpty(channelDto.getPeerWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(channelDto.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            String name = channelDto.getUserName();
            log.debug(name);

            String result = userService.loadUserFromPersistence(name, channelDto.getPassWord(), channelDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = channelService.constructChannel(channelDto.getChannelName(), channelDto.getPeerWithOrg());
                if (response == "Channel created successfully") {
                    return new ResultInfo(ResponseCodeEnum.SUCCESS, "channel created successfully");
                } else {
                    return new ResultInfo(ResponseCodeEnum.FAILURE, "Something went wrong");
                }

            } else {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "Something went wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo joinChannel(@RequestBody ChannelDto channelDto) throws Exception {
        try {
            if (StringUtils.isEmpty(channelDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
//            if (StringUtils.isEmpty(channelDto.getPassWord())) {
//                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
//            }
            if (StringUtils.isEmpty(channelDto.getPeerWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(channelDto.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            String uname = channelDto.getUserName();
            log.debug(uname);

            String result = userService.loadUserFromPersistence(uname, channelDto.getPassWord(), channelDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = channelService.joinChannel(channelDto.getChannelName(), channelDto.getPeerWithOrg());
                if (response == "Channel joined successfully") {
                    return new ResultInfo(ResponseCodeEnum.SUCCESS, "channel join successfully");
                } else {
                    return new ResultInfo(ResponseCodeEnum.FAILURE, "Something went wrong");
                }
            } else {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "Something went wrong");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }
}
