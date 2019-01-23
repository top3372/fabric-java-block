package com.ideal.blockchain.controller.block;

import com.ideal.blockchain.dto.request.ChaincodeNameDto;
import com.ideal.blockchain.dto.request.FunctionAndArgsDto;
import com.ideal.blockchain.dto.request.InvokeChainCodeArgsDto;
import com.ideal.blockchain.dto.request.QueryArgsDto;
import com.ideal.blockchain.dto.response.ResultInfo;
import com.ideal.blockchain.enums.ResponseCodeEnum;
import com.ideal.blockchain.service.block.ChainCodeService;
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
@RequestMapping("/chaincode")
public class ChainCodeController {

    @Autowired
    private ChainCodeService chainCodeService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/install", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo installChaincode(@RequestBody ChaincodeNameDto chaincodeName) {
        try {
            if (StringUtils.isEmpty(chaincodeName.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
//            if (StringUtils.isEmpty(chaincodeName.getPassWord())) {
//                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
//            }
            if (StringUtils.isEmpty(chaincodeName.getPeerWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(chaincodeName.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            if (StringUtils.isEmpty(chaincodeName.getChainCodeName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeName in request body");
            }
            if (StringUtils.isEmpty(chaincodeName.getChainCodeVersion())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeVersion in request body");
            }

            String name = chaincodeName.getUserName();
            String result = userService.loadUserFromPersistence(name, chaincodeName.getPassWord(), chaincodeName.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {

                String response = chainCodeService.installChaincode(name, chaincodeName.getPeerWithOrg(), chaincodeName.getChannelName(),
                        chaincodeName.getChainCodeName(), chaincodeName.getChainCodeVersion());
                if (response == "Chaincode installed successfully") {
                    return new ResultInfo(ResponseCodeEnum.SUCCESS, response);

                } else {
                    return new ResultInfo(ResponseCodeEnum.FAILURE, response);

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

    /**
     * takes input as function name (init), arguments , chaincode name and
     * authorization token.
     *
     * @return status as string
     */
    @RequestMapping(value = "/instantiate", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo instantiateChaincode(@RequestBody FunctionAndArgsDto chaincodeDto) {
        try {
            if (StringUtils.isEmpty(chaincodeDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
//            if (StringUtils.isEmpty(chaincodeDto.getPassWord())) {
//                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
//            }
            if ((chaincodeDto.getFunction()) == null) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "function not present in method body");
            }
            if ((chaincodeDto.getPeerWithOrgs()) == null || chaincodeDto.getPeerWithOrgs().length == 0) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrgs in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getBelongWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChainCodeName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeName in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChainCodeVersion())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeVersion in request body");
            }
            if (chaincodeDto.getArgs() == null) {

                return new ResultInfo(ResponseCodeEnum.FAILURE, "args not present in method body");
            }
            String uname = chaincodeDto.getUserName();
            String result = userService.loadUserFromPersistence(uname, chaincodeDto.getPassWord(), chaincodeDto.getBelongWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.instantiateChaincode(uname, chaincodeDto.getBelongWithOrg(),chaincodeDto.getPeerWithOrgs(),
                        chaincodeDto.getChannelName(), chaincodeDto.getChainCodeName(), chaincodeDto.getFunction(),
                        chaincodeDto.getArgs(), chaincodeDto.getChainCodeVersion());
                if (response == "Chaincode instantiated Successfully") {
                    return new ResultInfo(ResponseCodeEnum.SUCCESS, response);
                } else {
                    return new ResultInfo(ResponseCodeEnum.FAILURE, response);
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

    /**
     * takes input as function name (init), arguments , chaincode name and
     * authorization token.
     *
     * @return status as string
     */
    @RequestMapping(value = "/upgrade", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo upgradeChaincode(@RequestBody FunctionAndArgsDto chaincodeDto) throws Exception {
        try {
            if (StringUtils.isEmpty(chaincodeDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
//            if (StringUtils.isEmpty(chaincodeDto.getPassWord())) {
//                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
//            }
            if ((chaincodeDto.getPeerWithOrgs()) == null || chaincodeDto.getPeerWithOrgs().length == 0) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrgs in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getBelongWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChainCodeName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeName in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChainCodeVersion())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeVersion in request body");
            }
            if ((chaincodeDto.getFunction()) == null) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "function not present in method body");
            }
            if (chaincodeDto.getArgs() == null) {

                return new ResultInfo(ResponseCodeEnum.FAILURE, "args not present in method body");
            }
            String uname = chaincodeDto.getUserName();
            String result = userService.loadUserFromPersistence(uname, chaincodeDto.getPassWord(), chaincodeDto.getBelongWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.updateChaincode(uname, chaincodeDto.getBelongWithOrg(),chaincodeDto.getPeerWithOrgs(),
                        chaincodeDto.getChannelName(), chaincodeDto.getChainCodeName(), chaincodeDto.getFunction(),
                        chaincodeDto.getArgs(), chaincodeDto.getChainCodeVersion()
                );
                if (response == "Chaincode upgrade Successfully") {
                    return new ResultInfo(ResponseCodeEnum.SUCCESS, response);
                } else {
                    return new ResultInfo(ResponseCodeEnum.FAILURE, response);
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

    /**
     * takes input as function name (invoke), arguments , chaincode name and
     * authorization token.
     *
     * @return status as string
     */
    @RequestMapping(value = "/invoke", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo invokeChaincode(@RequestBody InvokeChainCodeArgsDto chaincodeDto) throws Exception {
        try {
            if (StringUtils.isEmpty(chaincodeDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
//            if (StringUtils.isEmpty(chaincodeDto.getPassWord())) {
//                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
//            }
            if ((chaincodeDto.getFunction()) == null) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "function not present in method body");
            }

            if ((chaincodeDto.getPeerWithOrgs()) == null || chaincodeDto.getPeerWithOrgs().length == 0) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrgs in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getBelongWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChainCodeName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeName in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChainCodeVersion())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeVersion in request body");
            }
            if (chaincodeDto.getArgs() == null) {

                return new ResultInfo(ResponseCodeEnum.FAILURE, "args not present in method body");
            }
            String uname = chaincodeDto.getUserName();
            String result = userService.loadUserFromPersistence(uname, chaincodeDto.getPassWord(), chaincodeDto.getBelongWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.invokeChaincode(uname, chaincodeDto.getBelongWithOrg(), chaincodeDto.getPeerWithOrgs(), chaincodeDto.getChannelName(),
                        chaincodeDto.getChainCodeName(), chaincodeDto.getFunction(), chaincodeDto.getArgs(), chaincodeDto.getChainCodeVersion());
                if (response == "Transaction invoked successfully") {
                    return new ResultInfo(ResponseCodeEnum.SUCCESS, response);
                } else {
                    return new ResultInfo(ResponseCodeEnum.FAILURE, response);
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

    /**
     * @return payload returned from the chaincode
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo queryChaincode(@RequestBody QueryArgsDto chaincodeDto) throws Exception {
        try {
            if (StringUtils.isEmpty(chaincodeDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
//            if (StringUtils.isEmpty(chaincodeDto.getPassWord())) {
//                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
//            }
            if (StringUtils.isEmpty(chaincodeDto.getPeerWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChainCodeName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeName in request body");
            }
            if (StringUtils.isEmpty(chaincodeDto.getChainCodeVersion())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter ChainCodeVersion in request body");
            }
            if ((chaincodeDto.getFunction()) == null) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "function not present in method body");
            }
            if (chaincodeDto.getArgs() == null) {

                return new ResultInfo(ResponseCodeEnum.FAILURE, "args not present in method body");
            }
            String uname = chaincodeDto.getUserName();

            String result = userService.loadUserFromPersistence(uname, chaincodeDto.getPassWord(), chaincodeDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.queryChainCode(uname, chaincodeDto.getPeerWithOrg(), chaincodeDto.getChannelName(),
                        chaincodeDto.getChainCodeName(), chaincodeDto.getFunction(), chaincodeDto.getArgs(), chaincodeDto.getChainCodeVersion());
                if (response != "Caught an exception while quering chaincode") {
                    return new ResultInfo(ResponseCodeEnum.SUCCESS, response);
                } else {
                    return new ResultInfo(ResponseCodeEnum.FAILURE, response);
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
