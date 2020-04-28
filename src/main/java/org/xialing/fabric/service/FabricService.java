package org.xialing.fabric.service;


import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import org.xialing.common.dto.route.invoke.InvokeAsyncQueryRequest;
import org.xialing.common.dto.route.invoke.InvokeAsyncQueryResult;
import org.xialing.common.dto.route.invoke.InvokeRequest;
import org.xialing.common.dto.route.invoke.InvokeResult;
import org.xialing.common.dto.route.query.QueryRequest;
import org.xialing.common.dto.route.query.QueryResult;
import org.xialing.common.enums.AsyncInvokeTypeEnum;
import org.xialing.common.enums.ResponseCodeEnum;
import org.xialing.fabric.template.FabricTemplate;
import org.xialing.fabric.utils.FabricUtils;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author leon
 * @version 1.0
 * @date 2020/2/27 14:10
 */
@Service
@Slf4j
public class FabricService extends CommonService{


    public InvokeResult invoke(InvokeRequest invokeRequest) {
        HFClient client = HFClient.createNewInstance();
        InvokeResult invokeResult = null;
        try {
            Channel currentChannel = super.initChannel(client, invokeRequest);

            FabricTemplate fabricTemplate = new FabricTemplate();
            FabricTemplate.setClient(client);

            List<String> peers = FabricUtils.converPeerNodeKey(invokeRequest.getStrategyOrgList());

            ChaincodeID chainCodeId = ChaincodeID.newBuilder()
                    .setName(invokeRequest.getBlockChainContractDTO().getContractKey())
                    .setVersion(invokeRequest.getBlockChainContractDTO().getVersion())
                    .setPath("").build();
            invokeResult = fabricTemplate.transaction
                    (currentChannel, invokeRequest.getBlockChainContractFunDTO().getFunKey(),
                            invokeRequest.getArgs(),
                            chainCodeId,
                            peers, invokeRequest.getBlockChainContractFunDTO().getAsyncFlag());
            invokeResult.setOrderNo(invokeRequest.getOrderNo());
            invokeResult.setRequestId(IdUtil.fastUUID());
            invokeResult.setRequestContext(JSONUtil.toJsonStr(invokeRequest));
            invokeResult.setReturnNo(invokeResult.getTxId());
            if (AsyncInvokeTypeEnum.ASYNC.getCode().equals(invokeRequest.getBlockChainContractFunDTO().getAsyncFlag())) {
                invokeResult.setStatus(ResponseCodeEnum.PROCESSING.getCode());
            } else {
                invokeResult.setStatus(ResponseCodeEnum.SUCCESS.getCode());
            }
            invokeResult.setResponseContext(JSONUtil.toJsonStr(invokeResult));
        }catch (Exception e){
            log.error("Fabric SDK invoke 出错: " + e.getMessage());
            e.printStackTrace();
            invokeResult = new InvokeResult();
            invokeResult.setOrderNo(invokeRequest.getOrderNo());
            invokeResult.setRequestId(IdUtil.fastUUID());
            invokeResult.setRequestContext(JSONUtil.toJsonStr(invokeRequest));
            invokeResult.setStatus(ResponseCodeEnum.PROCESS_ERROR.getCode());
            invokeResult.setErrorMsg(e.getMessage());
        }
        return invokeResult;
    }

    public QueryResult query(QueryRequest queryRequest) {
        HFClient client = HFClient.createNewInstance();
        QueryResult queryResult = null;
        try {
            Channel currentChannel = super.initChannel(client, queryRequest);

            FabricTemplate fabricTemplate = new FabricTemplate();
            FabricTemplate.setClient(client);
            List<String> peers = FabricUtils.converPeerNodeKey(queryRequest.getStrategyOrgList());

            ChaincodeID chainCodeId = ChaincodeID.newBuilder()
                    .setName(queryRequest.getBlockChainContractDTO().getContractKey())
                    .setVersion(queryRequest.getBlockChainContractDTO().getVersion())
                    .setPath("").build();
            queryResult = fabricTemplate.query(currentChannel, queryRequest.getBlockChainContractFunDTO().getFunKey(),
                    queryRequest.getArgs(),
                    chainCodeId, peers);
            queryResult.setOrderNo(queryRequest.getOrderNo());
            queryResult.setRequestId(IdUtil.fastUUID());
            queryResult.setRequestContext(JSONUtil.toJsonStr(queryRequest));

            queryResult.setStatus(ResponseCodeEnum.SUCCESS.getCode());
            queryResult.setResponseContext(JSONUtil.toJsonStr(queryResult));

        }catch(Exception e){
            log.error("Fabric SDK query 出错: " + e.getMessage());
            e.printStackTrace();
            queryResult = new QueryResult();
            queryResult.setOrderNo(queryRequest.getOrderNo());
            queryResult.setRequestId(IdUtil.fastUUID());
            queryResult.setRequestContext(JSONUtil.toJsonStr(queryRequest));
            queryResult.setStatus(ResponseCodeEnum.PROCESS_ERROR.getCode());
            queryResult.setErrorMsg(e.getMessage());
        }
        return queryResult;
    }

    public InvokeAsyncQueryResult asyncQueryResult(InvokeAsyncQueryRequest invokeAsyncQueryRequest)  {
        HFClient client = HFClient.createNewInstance();
        Channel currentChannel = super.initChannel(client, invokeAsyncQueryRequest);

        FabricTemplate fabricTemplate = new FabricTemplate();
        FabricTemplate.setClient(client);

        InvokeAsyncQueryResult result = fabricTemplate.asyncQueryResult(currentChannel,invokeAsyncQueryRequest.getTxId());

        result.setOrderNo(invokeAsyncQueryRequest.getOrderNo());
        result.setRequestContext(JSONUtil.toJsonStr(invokeAsyncQueryRequest));
        result.setReturnNo(invokeAsyncQueryRequest.getTxId());
        result.setResponseContext(JSONUtil.toJsonStr(result));
        return result;
    }




}
