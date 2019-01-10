package com.ideal.blockchain.service.block;

import com.alibaba.fastjson.JSONObject;
import com.ideal.blockchain.config.HyperledgerConfiguration;
import com.ideal.blockchain.model.Org;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: LeonMa
 * @date: 2019/01/10 12:02
 */
@Slf4j
@Service
public class BlockService {

    @Autowired
    private HyperledgerConfiguration hyperledgerConfiguration;

    @Autowired
    private ChannelService channelService;

    public BlockInfo blockchainInfo(String name, String peerWithOrg, String channelName) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);

        Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);
        client.setUserContext(sampleOrg.getUser(name));
        //Set<Peer> peerSet = sampleOrg.getPeers();

        Channel channel = channelService.reconstructChannel(peerWithOrg, channelName, client);

        BlockchainInfo channelInfo = channel.queryBlockchainInfo();
        log.info("Channel info for : " + channelName);
        log.info("Channel height: " + channelInfo.getHeight());
        String chainCurrentHash = Hex.toHexString(channelInfo.getCurrentBlockHash());
        String chainPreviousHash = Hex.toHexString(channelInfo.getPreviousBlockHash());
        log.info("Chain current block hash: " + chainCurrentHash);
        log.info("Chain previous block hash: " + chainPreviousHash);

        BlockInfo returnedBlock = channel.queryBlockByNumber(channelInfo.getHeight() - 1);
        String previousHash = Hex.toHexString(returnedBlock.getPreviousHash());
        log.info("queryBlockByNumber returned correct block with blockNumber " + returnedBlock.getBlockNumber()
                + " \n previous_hash " + previousHash);

        byte[] hashQuery = returnedBlock.getPreviousHash();
        returnedBlock = channel.queryBlockByHash(hashQuery);
        log.info("queryBlockByHash returned block with blockNumber " + returnedBlock.getBlockNumber());

        return returnedBlock;
    }


    public String blockChainInfoByTxnId(String name, String peerWithOrg, String channelName,String txId) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);

        Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);
        client.setUserContext(sampleOrg.getUser(name));

        Channel channel = channelService.reconstructChannel(peerWithOrg, channelName, client);

        Map<String,Object> blockMap = new HashMap<>();
        BlockInfo blockInfo = channel.queryBlockByTransactionID(txId);
        blockMap.put("blockNumber",blockInfo.getBlockNumber());
        blockMap.put("dataHash", blockInfo.getDataHash());
        blockMap.put("previousHash",blockInfo.getPreviousHash());


        return JSONObject.toJSONString(blockMap);
    }

}
