package blockchain.controller;

import blockchain.dto.*;
import blockchain.dto.response.ResultInfo;
import blockchain.enums.ResponseCodeEnum;


import blockchain.service.ChainCodeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChaincodeController {

    private static final Logger logger = LoggerFactory.getLogger(ChaincodeController.class);
//    private static final long EXPIRATIONTIME = 9000000;


    @Autowired
    private ChainCodeServiceImpl chainCodeService;

    @RequestMapping(value = "/enroll", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo enroll(@RequestBody UserDto user) {
        try {
            if (StringUtils.isEmpty(user.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
            if (StringUtils.isEmpty(user.getPassWord())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter password in request body");
            }
            if (StringUtils.isEmpty(user.getPeerWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            String result = chainCodeService.register(user.getUserName(), user.getPassWord(), user.getPeerWithOrg());
            return new ResultInfo(ResponseCodeEnum.SUCCESS, result);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }

    /**
     * Return the channel that has been created , it takes the JWT token of the
     * user that is constructing it.
     *
     * @returns the channel that has been created
     */
    @RequestMapping(value = "/api/construct", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo createChannel(@RequestBody ChannelDto channelDto) {
        try {

            if (StringUtils.isEmpty(channelDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
            if (StringUtils.isEmpty(channelDto.getPassWord())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
            }
            if (StringUtils.isEmpty(channelDto.getPeerWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(channelDto.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            String name = channelDto.getUserName();
            logger.debug(name);

            String result = chainCodeService.loadUserFromPersistence(name, channelDto.getPassWord(), channelDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.constructChannel(channelDto.getChannelName(), channelDto.getPeerWithOrg());
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
            logger.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }


    /**
     * takes as input chaincode name and authorization token and returns status
     * message as string for installation of chaincode.
     *
     * @param chaincodeName
     * @return the status as string
     * @throws Exception
     */
    @RequestMapping(value = "/api/install", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo installChaincode(@RequestBody ChaincodeNameDto chaincodeName) {
        try {
            if (StringUtils.isEmpty(chaincodeName.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
            if (StringUtils.isEmpty(chaincodeName.getPassWord())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
            }
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
            String result = chainCodeService.loadUserFromPersistence(name, chaincodeName.getPassWord(), chaincodeName.getPeerWithOrg());
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
            logger.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }

    /**
     * takes input as function name (init), arguments , chaincode name and
     * authorization token.
     *
     * @return status as string
     */
    @RequestMapping(value = "/api/instantiate", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo instantiateChaincode(@RequestBody FunctionAndArgsDto chaincodeDto) {
        try {
            if (StringUtils.isEmpty(chaincodeDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
            if (StringUtils.isEmpty(chaincodeDto.getPassWord())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
            }
            if ((chaincodeDto.getFunction()) == null) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "function not present in method body");
            }
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
            if (chaincodeDto.getArgs() == null) {

                return new ResultInfo(ResponseCodeEnum.FAILURE, "args not present in method body");
            }
            String uname = chaincodeDto.getUserName();
            String result = chainCodeService.loadUserFromPersistence(uname, chaincodeDto.getPassWord(), chaincodeDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.instantiateChaincode(uname, chaincodeDto.getPeerWithOrg(),
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
            logger.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }

    /**
     * takes input as function name (init), arguments , chaincode name and
     * authorization token.
     *
     * @return status as string
     */
    @RequestMapping(value = "/api/upgrade", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo upgradeChaincode(@RequestBody FunctionAndArgsDto chaincodeDto) throws Exception {
        try {
            if (StringUtils.isEmpty(chaincodeDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
            if (StringUtils.isEmpty(chaincodeDto.getPassWord())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
            }
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
            String result = chainCodeService.loadUserFromPersistence(uname, chaincodeDto.getPassWord(), chaincodeDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.updateChaincode(uname, chaincodeDto.getPeerWithOrg(),
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
            logger.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }

    /**
     * takes input as function name (invoke), arguments , chaincode name and
     * authorization token.
     *
     * @return status as string
     */
    @RequestMapping(value = "/api/invoke", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo invokeChaincode(@RequestBody InvokeChainCodeArgsDto chaincodeDto) throws Exception {
        try {
            if (StringUtils.isEmpty(chaincodeDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
            if (StringUtils.isEmpty(chaincodeDto.getPassWord())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
            }
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
            String result = chainCodeService.loadUserFromPersistence(uname, chaincodeDto.getPassWord(), chaincodeDto.getBelongWithOrg());
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
            logger.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }

    /**
     * @return payload returned from the chaincode
     */
    @RequestMapping(value = "/api/query", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo queryChaincode(@RequestBody QueryArgsDto chaincodeDto) throws Exception {
        try {
            if (StringUtils.isEmpty(chaincodeDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
            if (StringUtils.isEmpty(chaincodeDto.getPassWord())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
            }
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

            String result = chainCodeService.loadUserFromPersistence(uname, chaincodeDto.getPassWord(), chaincodeDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.queryChaincode(uname, chaincodeDto.getPeerWithOrg(), chaincodeDto.getChannelName(),
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
            logger.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }

    @RequestMapping(value = "/api/join", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo joinChannel(@RequestBody ChannelDto channelDto) throws Exception {
        try {
            if (StringUtils.isEmpty(channelDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
            if (StringUtils.isEmpty(channelDto.getPassWord())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
            }
            if (StringUtils.isEmpty(channelDto.getPeerWithOrg())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter peerWithOrg in request body");
            }
            if (StringUtils.isEmpty(channelDto.getChannelName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter channelName in request body");
            }
            String uname = channelDto.getUserName();
            logger.debug(uname);

            String result = chainCodeService.loadUserFromPersistence(uname, channelDto.getPassWord(), channelDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.joinChannel(channelDto.getChannelName(), channelDto.getPeerWithOrg());
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
            logger.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }


    @RequestMapping(value = "/api/block/txid", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo blockInfoByTxId(@RequestBody BlockDto blockDto) throws Exception {
        try {
            if (StringUtils.isEmpty(blockDto.getUserName())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter username in reques body!");
            }
            if (StringUtils.isEmpty(blockDto.getPassWord())) {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "please enter passwords in request body");
            }
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
            logger.debug(uname);

            String result = chainCodeService.loadUserFromPersistence(uname, blockDto.getPassWord(), blockDto.getPeerWithOrg());
            if (result == "Successfully loaded member from persistence") {
                String response = chainCodeService.blockChainInfoByTxnId(blockDto.getUserName(),blockDto.getPeerWithOrg(),blockDto.getChannelName(),blockDto.getTxId());

                return new ResultInfo(ResponseCodeEnum.SUCCESS, response);

            } else {
                return new ResultInfo(ResponseCodeEnum.FAILURE, "Something went wrong");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResultInfo(ResponseCodeEnum.FAILURE, e.getMessage());
        }
    }
}
