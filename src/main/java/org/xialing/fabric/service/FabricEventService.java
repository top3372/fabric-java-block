package org.xialing.fabric.service;


import org.xialing.common.dto.block.BlockChainContractFunDTO;
import org.xialing.common.dto.block.BlockChainContractFunEventDTO;
import org.xialing.common.dto.route.event.ContractEvent;
import org.xialing.common.dto.route.event.ContractEventListenerRequest;
import org.xialing.fabric.context.ChannelContext;
import org.xialing.fabric.listener.FabricEventListener;
import org.xialing.fabric.model.ChannelBean;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.springframework.stereotype.Service;
import org.xialing.fabric.remote.RemoteBlockEventService;

import javax.annotation.Resource;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/6 14:41
 */
@Service
public class FabricEventService extends CommonService {

    @Resource
    private RemoteBlockEventService remoteBlockEventService;

    public void registerContractListener(ContractEventListenerRequest contractEventListenerRequest) throws InvalidArgumentException {

        String channelName = contractEventListenerRequest.getBlockChainNetDTO().getBlockChainNetCode() +
                contractEventListenerRequest.getBlockChainChannelDTO().getChannelCode() +
                contractEventListenerRequest.getLeagueCode();


        ChannelBean channelBean = ChannelContext.getChannelContext(channelName);
        if(channelBean == null){
            HFClient client = HFClient.createNewInstance();
            Channel currentChannel = super.initChannel(client, contractEventListenerRequest);
            channelBean = new ChannelBean();
            channelBean.setChannel(currentChannel);
            ChannelContext.addChannelContext(channelName,channelBean);
        }

       for(BlockChainContractFunDTO funDTO : contractEventListenerRequest.getFunList()){
           for(BlockChainContractFunEventDTO eventDTO : funDTO.getEventDTOList()){

               ContractEvent contractEvent = new ContractEvent();
               contractEvent.setChainNetCode(contractEventListenerRequest.getBlockChainNetDTO().getBlockChainNetCode());
               contractEvent.setChainNetKey(contractEventListenerRequest.getBlockChainNetDTO().getBlockChainNetKey());
               contractEvent.setChannelCode(contractEventListenerRequest.getBlockChainChannelDTO().getChannelCode());
               contractEvent.setChannelKey(contractEventListenerRequest.getBlockChainChannelDTO().getChannelKey());
               contractEvent.setContractCode(contractEventListenerRequest.getBlockChainContractDTO().getContractCode());
               contractEvent.setContractKey(contractEventListenerRequest.getBlockChainContractDTO().getContractKey());
               contractEvent.setFunCode(funDTO.getFunCode());
               contractEvent.setFunKey(funDTO.getFunKey());
               contractEvent.setEventCode(eventDTO.getEventCode());
               contractEvent.setEventKey(eventDTO.getEventKey());
               contractEvent.setEvenType(eventDTO.getEventType());
               contractEvent.setLeagueCode(contractEventListenerRequest.getLeagueCode());

               String eventListenerHandle = FabricEventListener.setChainCodeEventListener
                       (channelBean.getChannel(), contractEvent,remoteBlockEventService);

               String channelContractName = contractEventListenerRequest.getBlockChainNetDTO().getBlockChainNetCode() +
                       contractEventListenerRequest.getBlockChainChannelDTO().getChannelCode() +
                       contractEventListenerRequest.getLeagueCode() +
                       contractEventListenerRequest.getBlockChainContractDTO().getContractCode() +
                       funDTO.getFunCode() +
                       eventDTO.getEventCode();

               ChannelContext.addChainCodeEventHandlerContext
                       (channelContractName,eventListenerHandle);

           }
           channelBean.setContractEventCount(channelBean.getContractEventCount() + 1);
       }



    }

    public void unRegisterContractListener(ContractEventListenerRequest contractEventListenerRequest) throws InvalidArgumentException {

        String channelName = contractEventListenerRequest.getBlockChainNetDTO().getBlockChainNetCode() +
                contractEventListenerRequest.getBlockChainChannelDTO().getChannelCode() +
                contractEventListenerRequest.getLeagueCode();

        ChannelBean channelBean = ChannelContext.getChannelContext(channelName);
        if(channelBean == null){
            HFClient client = HFClient.createNewInstance();
            Channel currentChannel = super.initChannel(client, contractEventListenerRequest);
            channelBean = new ChannelBean();
            channelBean.setChannel(currentChannel);
            ChannelContext.addChannelContext(channelName,channelBean);
        }

        for(BlockChainContractFunDTO funDTO : contractEventListenerRequest.getFunList()) {
            for (BlockChainContractFunEventDTO eventDTO : funDTO.getEventDTOList()) {

                String channelContractName = contractEventListenerRequest.getBlockChainNetDTO().getBlockChainNetCode() +
                        contractEventListenerRequest.getBlockChainChannelDTO().getChannelCode() +
                        contractEventListenerRequest.getLeagueCode() +
                        contractEventListenerRequest.getBlockChainContractDTO().getContractCode() +
                        funDTO.getFunCode() +
                        eventDTO.getEventCode();

                String eventListenerHandle = ChannelContext.getChainCodeEventHandlerContext
                        (channelContractName);

                channelBean.getChannel().unregisterBlockListener(eventListenerHandle);
                ChannelContext.removeChainCodeEventHandlerContext(channelContractName);

            }
            channelBean.setContractEventCount(channelBean.getContractEventCount() - 1);
            if (channelBean.getContractEventCount() == 0) {
                ChannelContext.removeChannelContext(channelName);
            }
        }
    }
}
