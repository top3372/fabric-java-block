package org.xialing.fabric.service;


import org.xialing.common.dto.route.base.request.BaseRequest;
import org.xialing.common.enums.ResponseCodeEnum;
import org.xialing.common.exception.ServiceException;
import org.xialing.fabric.utils.FabricUtils;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/6 14:42
 */
public class CommonService {


    public Channel initChannel(HFClient client, BaseRequest baseRequest){
        Channel currentChannel;
        try {
            currentChannel = FabricUtils.initChannel(client,baseRequest.getCurrentBlockChainOrgUserDTO(),baseRequest.getCurrentBlockChainOrgDTO(),
                    baseRequest.getBlockChainChannelDTO(),baseRequest.getBaasRouteDTO(),baseRequest.getBlockChainOrdererDTOs());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ResponseCodeEnum.CHANNEL_INVOKE_ERROR,"初始化区块链配置失败");
        }
        return currentChannel;
    }


}
