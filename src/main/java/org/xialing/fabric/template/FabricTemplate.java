package org.xialing.fabric.template;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.xialing.common.dto.route.invoke.InvokeAsyncQueryResult;
import org.xialing.common.dto.route.invoke.InvokeResult;
import org.xialing.common.dto.route.query.QueryResult;
import org.xialing.common.enums.AsyncInvokeTypeEnum;
import org.xialing.common.enums.ResponseCodeEnum;
import org.xialing.fabric.utils.ThreadPoolManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * @author leon
 * @version 1.0
 * @date 2020/2/27 14:42
 */
@Slf4j
public class FabricTemplate {


    private static HFClient client = null;

    public static void setClient(HFClient client) {
        FabricTemplate.client = client;
    }

    private void checkClient() {
        if (client == null) {
            throw new RuntimeException("Please set template fabric client");
        }
    }

    public QueryResult query(Channel chain, String function, String[] args, ChaincodeID chainCodeId, List<String> peerList) {
        return query(chain, function, args, chainCodeId, peerList, 1);
    }

    public QueryResult query(Channel chain, String function, String[] args, ChaincodeID chainCodeId,
                             List<String> peerList, int policy) {
        checkClient();
        String result = null;
        int verify = 0;
        List<Peer> endorserList = new ArrayList<Peer>();

        chain.getPeers().forEach(peer -> {
            if (peerList.contains(peer.getName())) {
                endorserList.add(peer);
            }
        });

        try {
            QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
            queryByChaincodeRequest.setArgs(args);
            queryByChaincodeRequest.setFcn(function);
            queryByChaincodeRequest.setChaincodeID(chainCodeId);

            Collection<ProposalResponse> queryProposals = chain.queryByChaincode(queryByChaincodeRequest, endorserList);
            List<String> resultData = new ArrayList<>();
            for (ProposalResponse proposalResponse : queryProposals) {
                if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                    log.error("Failed query proposal from peer {} status: {} . Messages: {} . Was verified : {}",
                            proposalResponse.getPeer().getName(), proposalResponse.getStatus(),
                            proposalResponse.getMessage(), proposalResponse.isVerified());
                } else {
                    verify++;
                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    if (result == null) {
                        result = payload;
                    }
                    else if (!result.equals(payload)) {
                        throw new RuntimeException("检测到节点数据不匹配！请检查智能合约是否同步或是否存在恶意节点！");
                    }
                    resultData.add(payload);
                }
            }

            if (verify < policy) {
                throw new RuntimeException("请求不满足背书策略");
            }
            QueryResult queryResult = new QueryResult();
            queryResult.setData((String[]) resultData.toArray());
            return queryResult;
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    public InvokeResult transaction(Channel chain, String function, String[] args, ChaincodeID chainCodeId,
                                    List<String> peerList, String asyncFlag) {
        return transaction(chain, function, args, chainCodeId, peerList,asyncFlag, 1);
    }

    public InvokeResult transaction(Channel chain, String function, String[] args, ChaincodeID chainCodeId,
                              List<String> peerList,String asyncFlag, int policy) {

        try {
            checkClient();
            List<Peer> endorserList = new ArrayList<Peer>();

            chain.getPeers().forEach(peer -> {
                if (peerList.contains(peer.getName())) {
                    endorserList.add(peer);
                }
            });


            TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
            transactionProposalRequest.setArgs(args);
            transactionProposalRequest.setFcn(function);
            transactionProposalRequest.setChaincodeID(chainCodeId);

            Collection<ProposalResponse> successful = new LinkedList<>();
            Collection<ProposalResponse> failed = new LinkedList<>();
            Collection<ProposalResponse> queryProposals = chain.sendTransactionProposal(transactionProposalRequest,
                    endorserList);

            String transactionId = "";
            String returnPayLoad = "";
            for (ProposalResponse proposalResponse : queryProposals) {
                if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                    log.error("Failed query proposal from peer {} status: {} . Messages: {} . Was verified : {}",
                            proposalResponse.getPeer().getName(), proposalResponse.getStatus(),
                            proposalResponse.getMessage(), proposalResponse.isVerified());
                    failed.add(proposalResponse);
                } else {
                    successful.add(proposalResponse);
                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    if (returnPayLoad == null) {
                        log.debug("payload return {}", payload);
                        returnPayLoad = payload;
                        log.info("#############################交易ID############################# " + proposalResponse.getTransactionID());
                        transactionId = proposalResponse.getTransactionID();
                    } else if (!returnPayLoad.equals(payload)) {
                        throw new RuntimeException("检测到节点数据不匹配！请检查智能合约是否同步或是否存在恶意节点！");
                    }
                }
            }
            if (successful.size() < policy) {
                throw new RuntimeException("请求不满足背书策略");
            }
            if(AsyncInvokeTypeEnum.ASYNC.getCode().equals(asyncFlag)){
                InvokeResult result = new InvokeResult();
                result.setTxId(transactionId);
                //异步上链
                ThreadPoolManager.newInstance().addExecuteTask(() -> sendTransaction(chain, successful));
                return result;
            }else {
                InvokeResult result = this.sendTransaction(chain, successful);
                return result;
            }
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }


    public InvokeResult sendTransaction(Channel chain,Collection<ProposalResponse> successful){
        CompletableFuture<BlockEvent.TransactionEvent> future = chain.sendTransaction(successful, chain.getOrderers());
        try {
            BlockEvent.TransactionEvent event = future.get();
            log.info("#############################区块号############################# " + event.getBlockEvent().getBlockNumber());
            log.info("#############################交易ID############################# " + event.getTransactionID());
            log.debug("{}", event.isValid());
            InvokeResult result = new InvokeResult();
            result.setBlockNo(event.getBlockEvent().getBlockNumber());
            result.setTxId(event.getTransactionID());
            return result;
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }


    public InvokeAsyncQueryResult asyncQueryResult(Channel chain, String transactionId){
        InvokeAsyncQueryResult result = new InvokeAsyncQueryResult();
        try {
            TransactionInfo transactionInfo = chain.queryTransactionByID(transactionId);
            BlockInfo blockInfo = chain.queryBlockByTransactionID(transactionId);
            result.setBlockId(blockInfo.getBlockNumber());
            result.setTxValidationCode(String.valueOf(transactionInfo.getValidationCode().getNumber()));
            result.setTxValidationMsg(transactionInfo.getValidationCode().getValueDescriptor().getFullName());
            if(transactionInfo.getValidationCode().getNumber() == 0) {
                result.setStatus(ResponseCodeEnum.SUCCESS.getCode());
            }else {
                result.setStatus(ResponseCodeEnum.PROCESS_ERROR.getCode());
            }
        }catch (Exception e) {
            log.error("", e);
            result.setTxValidationCode(ResponseCodeEnum.PROCESS_ERROR.getCode());
            result.setTxValidationMsg(e.getLocalizedMessage());
            result.setStatus(ResponseCodeEnum.PROCESS_ERROR.getCode());
        }
        return result;
    }

    public BlockchainInfo getBlockInfo(Channel chain) throws InvalidArgumentException, ProposalException {
        client.queryChannels((Peer) chain.getPeers().toArray()[0]);//通道总数
        client.queryInstalledChaincodes((Peer) chain.getPeers().toArray()[0]);//安装的合约数
        chain.queryInstantiatedChaincodes((Peer) chain.getPeers().toArray()[0]);//当前组织安装合约数
        chain.getDiscoveredChaincodeNames();//
        chain.getPeersOrganizationMSPIDs();//
        chain.getPeers();//peer总数
        chain.getOrderers();//排序总数
        chain.getOrderersOrganizationMSPIDs();//组织总数
        BlockchainInfo blockchainInfo = chain.queryBlockchainInfo();
        blockchainInfo.getHeight();//当前通道区块高度
        return null;
    }


}
