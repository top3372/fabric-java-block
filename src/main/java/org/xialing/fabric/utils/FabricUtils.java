package org.xialing.fabric.utils;

import org.xialing.common.dto.block.*;
import org.xialing.common.dto.route.BaasRouteDTO;
import org.xialing.fabric.config.FabricConfig;
import org.xialing.fabric.model.FabricEnrollment;
import org.xialing.fabric.model.FabricUser;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.hyperledger.fabric.sdk.Channel.PeerOptions.createPeerOptions;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/3 15:24
 */
public class FabricUtils {

    public static Channel initChannel(HFClient client, BlockChainOrgUserDTO userDTO, BlockChainOrgDTO currentOrgDTO,
                                      BlockChainChannelDTO channelDTO, BaasRouteDTO routeDTO, List<BlockChainOrdererDTO> ordererList) throws Exception {
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        // 实例化用户需要先在控制台-证书管理中申请客户端证书，申请的企业名称需要与当前登录账户实名认证的企业名称相同
        FabricUser user = new FabricUser(userDTO.getOrgUserName(), new FabricEnrollment(userDTO.getOrgUserKey(), userDTO.getOrgUserCert()),
                currentOrgDTO.getMspId());

        client.setUserContext(user);

        Channel chain = client.newChannel(channelDTO.getChannelKey());

        for(BlockChainOrdererDTO ordererDTO: ordererList) {
            chain.addOrderer(client.newOrderer(ordererDTO.getOrdererKey(), routeDTO.getEndPoint(),
                    FabricConfig.getOrderProperties(routeDTO.getSecretId(),routeDTO.getSecretKey(),ordererDTO.getOrdererKey())));
        }

        for(BlockChainOrgNodeDTO nodeDTO : currentOrgDTO.getNodeList()){
            chain.addPeer(client.newPeer(nodeDTO.getNodeKey(), routeDTO.getEndPoint(), FabricConfig.getPeerProperties(routeDTO.getSecretId(),
                    routeDTO.getSecretKey(),nodeDTO.getNodeKey())),
                    createPeerOptions().setPeerRoles(EnumSet.of(Peer.PeerRole.ENDORSING_PEER,
                            Peer.PeerRole.LEDGER_QUERY, Peer.PeerRole.CHAINCODE_QUERY, Peer.PeerRole.EVENT_SOURCE)));
            //registerEventsForFilteredBlocks()
        }


        chain.initialize();

        return chain;
    }


//    public static List<byte[]> convertArgs(String[] args){
//        List<byte[]> byteArgs = new ArrayList<>();
//        for(String arg : args){
//            byteArgs.add(arg.getBytes());
//        }
//        return byteArgs;
//    }

    public static List<String> converPeerNodeKey(List<BlockChainOrgDTO> orgList){
        List<String> peerKeyList = new ArrayList<>();
        for(BlockChainOrgDTO orgDTO : orgList){
            for(BlockChainOrgNodeDTO nodeDTO : orgDTO.getNodeList()){
                peerKeyList.add(nodeDTO.getNodeKey());
            }
        }
        return peerKeyList;
    }
}
