package com.ideal.blockchain.service.block;

import com.alibaba.fastjson.JSONObject;
import com.ideal.blockchain.config.HyperledgerConfiguration;
import com.ideal.blockchain.model.Org;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.*;
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

        Channel channel = channelService.reconstructChannel(peerWithOrg, channelName, client);

        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo();

        log.info("Channel info for : " + channelName);
        log.info("Channel height: " + blockchainInfo.getHeight());
        String chainCurrentHash = Hex.toHexString(blockchainInfo.getCurrentBlockHash());
        String chainPreviousHash = Hex.toHexString(blockchainInfo.getPreviousBlockHash());
        log.info("Chain current block hash: " + chainCurrentHash);
        log.info("Chain previous block hash: " + chainPreviousHash);

        BlockInfo returnedBlock = channel.queryBlockByNumber(blockchainInfo.getHeight() - 1);
        String previousHash = Hex.toHexString(returnedBlock.getPreviousHash());
        log.info("queryBlockByNumber returned correct block with blockNumber " + returnedBlock.getBlockNumber()
                + " \n previous_hash " + previousHash);

        byte[] hashQuery = returnedBlock.getPreviousHash();
        returnedBlock = channel.queryBlockByHash(hashQuery);
        log.info("queryBlockByHash returned block with blockNumber " + returnedBlock.getBlockNumber());

        return returnedBlock;
    }

    public TransactionInfo blockchainInfo(String name, String peerWithOrg, String channelName,String txId) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);

        Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);
        client.setUserContext(sampleOrg.getUser(name));

        Channel channel = channelService.reconstructChannel(peerWithOrg, channelName, client);

        TransactionInfo transactionInfo = channel.queryTransactionByID(txId);

        return transactionInfo;
    }


    public BlockInfo blockChainInfoByTxnId(String name, String peerWithOrg, String channelName,String txId) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);

        Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);
        client.setUserContext(sampleOrg.getUser(name));

        Channel channel = channelService.reconstructChannel(peerWithOrg, channelName, client);


        BlockInfo blockInfo = channel.queryBlockByTransactionID(txId);

        return blockInfo;
    }

}
