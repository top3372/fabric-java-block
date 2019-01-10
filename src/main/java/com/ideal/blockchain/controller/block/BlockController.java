package com.ideal.blockchain.controller.block;

import com.ideal.blockchain.dto.request.BlockDto;
import com.ideal.blockchain.dto.response.ResultInfo;
import com.ideal.blockchain.enums.ResponseCodeEnum;
import com.ideal.blockchain.service.block.BlockService;
import com.ideal.blockchain.service.block.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author: LeonMa
 * @date: 2019/01/10 12:03
 */
@RestController
@Slf4j
@RequestMapping("/blockInfo")
public class BlockController {

    @Autowired
    private BlockService blockService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/withTxid", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo blockInfoByTxId(@RequestBody BlockDto blockDto) throws Exception {
        try {
            if (StringUtils.isEmpty(blockDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
//            if (StringUtils.isEmpty(blockDto.getPassWord())) {
//                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
//            }
            if (StringUtils.isEmpty(blockDto.getPeerWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(blockDto.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            if (StringUtils.isEmpty(blockDto.getTxId())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter txId in request body");
            }
            String uname = blockDto.getUserName();
            log.debug(uname);

            String result = userService.loadUserFromPersistence(uname, blockDto.getPassWord(), blockDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = blockService.blockChainInfoByTxnId(blockDto.getUserName(),blockDto.getPeerWithOrg(),blockDto.getChannelName(),blockDto.getTxId());

                return new ResultInfo(ResponseCodeEnum.SUCCESS, response);

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
