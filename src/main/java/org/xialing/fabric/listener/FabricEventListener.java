package org.xialing.fabric.listener;


import cn.hutool.json.JSONUtil;
import org.xialing.common.dto.route.event.ContractEvent;
import org.xialing.fabric.model.EventPayLoad;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.xialing.fabric.remote.RemoteBlockEventService;
import org.xialing.fabric.utils.ThreadPoolManager;

import java.util.regex.*;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/5 10:15
 */
@Slf4j
public class FabricEventListener {

    public static String setChainCodeEventListener(Channel channel,
                                                 ContractEvent contractEvent,
                                                 RemoteBlockEventService remoteBlockEventService)
            throws InvalidArgumentException {

        ChaincodeEventListener chaincodeEventListener = (handle, blockEvent, chaincodeEvent) -> {


            log.info("RECEIVED CHAINCODE EVENT with handle: " +
                    handle +
                    ", chaincodeId: " + chaincodeEvent.getChaincodeId() +
                    ", chaincode event name: " + chaincodeEvent.getEventName() +
                    ", transactionId: " + chaincodeEvent.getTxId() +
                    ", event Payload: " + new String(chaincodeEvent.getPayload()));
            //解析PayLoad 对比 是否 同一联盟信息
            EventPayLoad eventPayLoad = JSONUtil.toBean(new String(chaincodeEvent.getPayload()),EventPayLoad.class);
            if(contractEvent.getLeagueCode().equals(eventPayLoad.getLeagueCode())) {
                contractEvent.setEventPayLoad(new String(chaincodeEvent.getPayload()));
                contractEvent.setBlockNo(blockEvent.getBlockNumber());
                contractEvent.setTxId(chaincodeEvent.getTxId());
                //调用接口返回事件信息
                ThreadPoolManager.newInstance().addExecuteTask(() -> remoteBlockEventService.recordBlockEvent(contractEvent));
            }

        };
        // chaincode events.
        String chaincodeEventHandler = channel.registerChaincodeEventListener(Pattern.compile(".*"),
                Pattern.compile(Pattern.quote(contractEvent.getEventKey())), chaincodeEventListener);
        return chaincodeEventHandler;
    }


}
