package com.ideal.blockchain.service.block;

import com.ideal.blockchain.config.HyperledgerConfiguration;
import com.ideal.blockchain.model.Org;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.exception.TransactionEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.Channel.NOfEvents.createNofEvents;

/**
 * @author: LeonMa
 * @date: 2019/01/10 12:01
 */
@Service
@Slf4j
public class ChainCodeService {

    @Autowired
    private HyperledgerConfiguration hyperledgerConfiguration;

    @Autowired
    private ChannelService channelService;

    Type CHAIN_CODE_LANG = Type.GO_LANG;

    private void waitOnFabric(int additional) {

    }



    public String installChaincode(String name, String peerWithOrg, String channelName,
                                   String chaincodeName, String chainCodeVersion) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);

        hyperledgerConfiguration.loadOrderersAndPeers(client, peerWithOrg);
        Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);
        client.setUserContext(sampleOrg.getPeerAdmin());

        ChaincodeID chaincodeID = hyperledgerConfiguration.getChaincodeId(chaincodeName, chainCodeVersion);

        log.info("Running channel " + channelName);

        log.info("Creating install proposal");
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);
        installProposalRequest.setChaincodeSourceLocation(new File(HyperledgerConfiguration.PATH + "/artifacts/"));
        installProposalRequest.setChaincodeVersion(chainCodeVersion);
        installProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);
        installProposalRequest.setUserContext(sampleOrg.getPeerAdmin());
        log.info("Sending install proposal");
        int numInstallProposal = 0;

        Collection<ProposalResponse> responses;
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        Collection<Peer> peers = sampleOrg.getPeers();
        numInstallProposal = numInstallProposal + peers.size();
        responses = client.sendInstallProposal(installProposalRequest, peers);

        for (ProposalResponse response : responses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                log.info("Successful install proposal response Txid: " + response.getTransactionID() + " from peer " + response.getPeer().getName());
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        //   }
        log.info("Received " + numInstallProposal + " install proposal responses. Successful+verified: " + successful.size() + " . Failed: " + failed.size());

        if (failed.size() > 0) {
            ProposalResponse first = failed.iterator().next();
            log.error("Not enough endorsers for install :" + successful.size() + ".  " + first.getMessage());
            return "Not enough endorsers for install :" + first.getMessage();
        }


        return "Chaincode installed successfully";

    }


    public String instantiateChaincode(String name, String belongWithOrg, String[] peerWithOrgs, String channelName, String chaincodeName, String chaincodeFunction, String[] chaincodeArgs, String chainCodeVersion) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);

        client.setUserContext(HyperledgerConfiguration.config.getSampleOrg(belongWithOrg).getPeerAdmin());

        ChaincodeID chaincodeID = hyperledgerConfiguration.getChaincodeId(chaincodeName, chainCodeVersion);
        Channel channel = channelService.reconstructChannel(peerWithOrgs, channelName, client);

        log.info("Running channel " + channelName);


        Collection<Orderer> orderers = channel.getOrderers();

        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(HyperledgerConfiguration.config.getProposalWaitTime());
        instantiateProposalRequest.setChaincodeID(chaincodeID);
        instantiateProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);
        instantiateProposalRequest.setFcn(chaincodeFunction);
        instantiateProposalRequest.setArgs(chaincodeArgs);
        instantiateProposalRequest.setChaincodeVersion(chainCodeVersion);
        instantiateProposalRequest.setUserContext(HyperledgerConfiguration.config.getSampleOrg(belongWithOrg).getPeerAdmin());

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm);

        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy
                .fromYamlFile(new File(HyperledgerConfiguration.PATH + "/artifacts/chaincodeendorsementpolicy.yaml"));
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        Collection<ProposalResponse> responses;
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        log.info("Sending instantiateProposalRequest to all peers with arguments: " + chaincodeArgs);
        successful.clear();
        failed.clear();

        responses = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());

        for (ProposalResponse response : responses) {
            if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
                log.info("Succesful instantiate proposal response Txid: " + response.getTransactionID() + " from peer " + response.getPeer().getName());
            } else {
                failed.add(response);
            }
        }
        log.info("Received " + responses.size() + " instantiate proposal responses. Successful+verified: " + successful.size() + " . Failed: " + failed.size());
        if (failed.size() > 0) {
            for (ProposalResponse fail : failed) {

                log.info("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with " + fail.getMessage() + ", on peer" + fail.getPeer());

            }
            ProposalResponse first = failed.iterator().next();
            log.error("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with " + first.getMessage() + ". Was verified:" + first.isVerified());
            return "endorser failed";
        }
        log.info("Sending instantiateTransaction to orderer ");
        log.info("orderers" + orderers);
        //Specify what events should complete the interest in this transaction. This is the default
        // for all to complete. It's possible to specify many different combinations like
        //any from a group, all from one group and just one from another or even None(NOfEvents.createNoEvents).
        // See. Channel.NOfEvents
        Channel.NOfEvents nOfEvents = createNofEvents();
        if (!channel.getPeers(EnumSet.of(Peer.PeerRole.EVENT_SOURCE)).isEmpty()) {
            nOfEvents.addPeers(channel.getPeers(EnumSet.of(Peer.PeerRole.EVENT_SOURCE)));
        }
        if (!channel.getEventHubs().isEmpty()) {
            nOfEvents.addEventHubs(channel.getEventHubs());
        }
        String result = channel.sendTransaction(successful, orderers,HyperledgerConfiguration.config.getSampleOrg(belongWithOrg).getPeerAdmin()).thenApply(transactionEvent -> {
            waitOnFabric(0);
//            BlockEvent blockEvent = transactionEvent.getBlockEvent();

            log.info("Finished instantiate transaction with transaction id " + transactionEvent.getTransactionID());
            return "Chaincode instantiated Successfully";
        }).exceptionally(e -> {
            e.printStackTrace();
            log.info(" failed with " + e.getClass().getName() + " exception " + e.getMessage());
            return " failed with " + e.getClass().getName() + " exception " + e.getMessage();
        }).get(HyperledgerConfiguration.config.getTransactionWaitTime(), TimeUnit.SECONDS);

        return result;
    }


    public String invokeChaincode(String name, String belongWithOrg, String[] peerWithOrgs, String channelName, String chaincodeName,
                                  String chaincodeFunction, String[] chaincodeArgs, String chainCodeVersion) throws Exception {

        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);

        client.setUserContext(HyperledgerConfiguration.config.getSampleOrg(belongWithOrg).getPeerAdmin());

        ChaincodeID chaincodeID = hyperledgerConfiguration.getChaincodeId(chaincodeName, chainCodeVersion);
        Channel channel = channelService.reconstructChannel(peerWithOrgs, channelName, client);

        log.info("Running channel " + channelName);


        log.debug("chaincodeFunction" + chaincodeFunction);
        log.debug("chaincodeArgs" + chaincodeArgs);


        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setChaincodeLanguage(Type.GO_LANG);
        transactionProposalRequest.setFcn(chaincodeFunction);
        transactionProposalRequest.setProposalWaitTime(HyperledgerConfiguration.config.getProposalWaitTime());
        transactionProposalRequest.setArgs(chaincodeArgs);
        transactionProposalRequest.setChaincodeVersion(chainCodeVersion);
        transactionProposalRequest.setUserContext(HyperledgerConfiguration.config.getSampleOrg(belongWithOrg).getUser(name));

//        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
//        chaincodeEndorsementPolicy
//                .fromYamlFile(new File(PATH + "/artifacts/chaincodeendorsementpolicy.yaml"));
//        transactionProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8)); /// This should be returned

        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        transactionProposalRequest.setTransientMap(tm2);

        log.info("sending transactionProposal to all peers with arguments: " + chaincodeFunction + "," + chaincodeArgs);
        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                log.info("Successful transaction proposal response Txid: " + response.getTransactionID() + " from peer " + response.getPeer().getName());
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        // Check that all the proposals are consistent with each other. We should have only one set
        // where all the proposals above are consistent. Note the when sending to Orderer this is done automatically.
        //  Shown here as an example that applications can invoke and select.
        // See org.hyperledger.mapper.sdk.proposal.consistency_validation config property.
        Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(transactionPropResp);
        if (proposalConsistencySets.size() != 1) {
            log.error(format("Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size()));
        }

        log.info("Received " + transactionPropResp.size() + " transaction proposal responses. Successful+verified: " + successful.size() + " . Failed: " + failed.size());
        if (failed.size() > 0) {
            ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
            log.error("Not enough endorsers for invoke:" + failed.size() + " endorser error: " +
                    firstTransactionProposalResponse.getMessage() +
                    ". Was verified: " + firstTransactionProposalResponse.isVerified());
            return firstTransactionProposalResponse.getMessage();
        }
        log.info("Successfully received transaction proposal responses.");
        ProposalResponse resp = successful.iterator().next();
        byte[] x = resp.getChaincodeActionResponsePayload(); // This is the data returned by the chaincode.
        String resultAsString = null;
        if (x != null) {
            resultAsString = new String(x, "UTF-8");
        }
        log.debug("getChaincodeActionResponseReadWriteSetInfo:::"
                + resp.getChaincodeActionResponseReadWriteSetInfo());
        ChaincodeID cid = resp.getChaincodeID();

        ////////////////////////////
        // Send Transaction Transaction to orderer
        log.info("Sending chaincode transaction " + chaincodeName + "_" + chaincodeFunction + " to orderer.");
        String result = channel.sendTransaction(successful,HyperledgerConfiguration.config.getSampleOrg(belongWithOrg).getUser(name)).thenApply(transactionEvent -> {

            waitOnFabric(0);

            log.info("transaction event is valid " + transactionEvent.isValid()); // must
            for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo info : transactionEvent.getTransactionActionInfos()) {
                log.info("*************" + info.getResponseMessage());
            }
            // be
            // valid
            // to
            // be
            // here.
            log.info("Finished invoke transaction with transaction id " + transactionEvent.getTransactionID());

            return "Transaction invoked successfully";
        }).exceptionally(e -> {
            if (e instanceof TransactionEventException) {
                BlockEvent.TransactionEvent te = ((TransactionEventException) e).getTransactionEvent();
                if (te != null) {
                    log.error(format("Transaction with txid " + te.getTransactionID() + " failed. " + e.getMessage()));
                }
            }

            log.error("failed with " + e.getClass().getName() + " exception " + e.getMessage());
            return "failed with " + e.getClass().getName() + " exception " + e.getMessage();
        }).get(HyperledgerConfiguration.config.getTransactionWaitTime(), TimeUnit.SECONDS);
        log.info("Transaction invoked " + result);

        return result;
    }


    public String queryChainCode(String name, String peerWithOrg, String channelName, String chaincodeName, String chaincodeFunction, String[] chaincodeArgs, String chainCodeVersion) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);
        client.setUserContext(HyperledgerConfiguration.config.getSampleOrg(peerWithOrg).getPeerAdmin());

        ChaincodeID chaincodeID = hyperledgerConfiguration.getChaincodeId(chaincodeName, chainCodeVersion);
        Channel channel = channelService.reconstructChannel(peerWithOrg, channelName, client);

        log.info("Running channel " + channelName);
        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(chaincodeArgs);
        queryByChaincodeRequest.setFcn(chaincodeFunction);
        queryByChaincodeRequest.setChaincodeID(chaincodeID);
        queryByChaincodeRequest.setChaincodeVersion(chainCodeVersion);
        queryByChaincodeRequest.setChaincodeLanguage(Type.GO_LANG);
        queryByChaincodeRequest.setUserContext(HyperledgerConfiguration.config.getSampleOrg(peerWithOrg).getUser(name));

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
        queryByChaincodeRequest.setTransientMap(tm2);


        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());
        for (ProposalResponse proposalResponse : queryProposals) {
            if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                log.error("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " + proposalResponse.getStatus() +
                        ". Messages: " + proposalResponse.getMessage()
                        + ". Was verified : " + proposalResponse.isVerified());
                throw new Exception(proposalResponse.getMessage());
            } else {
                String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                log.info("Query payload of b from peer" + proposalResponse.getPeer().getName() + " returned " + payload);
                return payload;
            }
        }

        return "Caught an exception while quering chaincode";
    }


    public String updateChaincode(String name, String belongWithOrg, String[] peerWithOrgs, String channelName, String chaincodeName, String chaincodeFunction, String[] chaincodeArgs, String chainCodeVersion) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);
        client.setUserContext(HyperledgerConfiguration.config.getSampleOrg(belongWithOrg).getPeerAdmin());
        ChaincodeID chaincodeID = hyperledgerConfiguration.getChaincodeId(chaincodeName, chainCodeVersion);
        Channel channel = channelService.reconstructChannel(peerWithOrgs, channelName, client);

        log.info("Running channel " + channelName);


        Collection<Orderer> orderers = channel.getOrderers();

        UpgradeProposalRequest upgradeProposalRequest = client.newUpgradeProposalRequest();
        upgradeProposalRequest.setProposalWaitTime(HyperledgerConfiguration.config.getProposalWaitTime());
        upgradeProposalRequest.setChaincodeID(chaincodeID);
        upgradeProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);
        upgradeProposalRequest.setFcn(chaincodeFunction);
        upgradeProposalRequest.setArgs(chaincodeArgs);
        upgradeProposalRequest.setChaincodeVersion(chainCodeVersion);
        upgradeProposalRequest.setUserContext(HyperledgerConfiguration.config.getSampleOrg(belongWithOrg).getPeerAdmin());

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "UpgradeProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "UpgradeProposalRequest".getBytes(UTF_8));
        upgradeProposalRequest.setTransientMap(tm);

        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy
                .fromYamlFile(new File(HyperledgerConfiguration.PATH + "/artifacts/chaincodeendorsementpolicy.yaml"));
        upgradeProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        Collection<ProposalResponse> responses;
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        log.info("Sending instantiateProposalRequest to all peers with arguments: " + chaincodeArgs);
        successful.clear();
        failed.clear();
        responses = channel.sendUpgradeProposal(upgradeProposalRequest, channel.getPeers());

        for (ProposalResponse response : responses) {
            if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
                log.info("Succesful update proposal response Txid: " + response.getTransactionID() + " from peer " + response.getPeer().getName());
            } else {
                failed.add(response);
            }
        }
        log.info("Received " + responses.size() + " update proposal responses. Successful+verified: " + successful.size() + " . Failed: " + failed.size());
        if (failed.size() > 0) {
            for (ProposalResponse fail : failed) {

                log.info("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with " + fail.getMessage() + ", on peer" + fail.getPeer());

            }
            ProposalResponse first = failed.iterator().next();
            log.error("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with " + first.getMessage() + ". Was verified:" + first.isVerified());
        }
        log.info("Sending updateTransaction to orderer ");
        log.info("orderers" + orderers);
        //Specify what events should complete the interest in this transaction. This is the default
        // for all to complete. It's possible to specify many different combinations like
        //any from a group, all from one group and just one from another or even None(NOfEvents.createNoEvents).
        // See. Channel.NOfEvents
        Channel.NOfEvents nOfEvents = createNofEvents();
        if (!channel.getPeers(EnumSet.of(Peer.PeerRole.EVENT_SOURCE)).isEmpty()) {
            nOfEvents.addPeers(channel.getPeers(EnumSet.of(Peer.PeerRole.EVENT_SOURCE)));
        }
        if (!channel.getEventHubs().isEmpty()) {
            nOfEvents.addEventHubs(channel.getEventHubs());
        }
        channel.sendTransaction(successful, orderers,HyperledgerConfiguration.config.getSampleOrg(belongWithOrg).getPeerAdmin()).thenApply(transactionEvent -> {
            waitOnFabric(0);
            BlockEvent blockEvent = transactionEvent.getBlockEvent();
            log.info("Finished update transaction with transaction id " + transactionEvent.getTransactionID());
            return "Finished update transaction with transaction id " + transactionEvent.getTransactionID();
        }).exceptionally(e -> {
            e.printStackTrace();
            log.info(" failed with " + e.getClass().getName() + " exception " + e.getMessage());
            return " failed with " + e.getClass().getName() + " exception " + e.getMessage();
        }).get(HyperledgerConfiguration.config.getTransactionWaitTime(), TimeUnit.SECONDS);

        return "Chaincode upgrade Successfully";
    }

}
