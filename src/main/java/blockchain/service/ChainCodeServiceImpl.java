package blockchain.service;


import blockchain.config.Config;
import blockchain.config.ConfigHelper;
import blockchain.config.ConnectionUtil;
import blockchain.model.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.TransactionEventException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.*;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.hyperledger.fabric.sdk.TransactionRequest.Type;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.Channel.NOfEvents.createNofEvents;
import static org.hyperledger.fabric.sdk.Channel.PeerOptions.createPeerOptions;
import static org.hyperledger.fabric.sdk.Channel.TransactionOptions.createTransactionOptions;


@PropertySource("hyperledger.properties")
@Service("chainCodeService")
public class ChainCodeServiceImpl implements ChainCodeService {

    private static final Logger logger = LoggerFactory.getLogger(ChainCodeServiceImpl.class);

    private static String PATH = System.getProperty("user.dir");


    private static final Config config = Config.getConfig();
    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_PWD}")
    private String adminPwd;

    private String FIXTURES_PATH = PATH + "/";

    @Value("${CHAIN_CODE_PATH}")
    private String chainCodePath;


    private final ConfigHelper configHelper = new ConfigHelper();

    private Collection<Org> SampleOrgs;


    Type CHAIN_CODE_LANG = Type.GO_LANG;


    /**
     * checking config at starting
     */
    private void checkConfig(HFClient client) throws Exception {

        SampleOrgs = config.getSampleOrgs();

        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        // Set up hfca for each sample org

        for (Org sampleOrg : SampleOrgs) {
            try {
                sampleOrg.setCAClient(HFCAClient.createNewInstance(sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * checking config at starting
     */
    private void loadOrderersAndPeers(HFClient client, String peerWtihOrg) throws Exception {

        // Set up hfca for each sample org

        Org sampleOrg = config.getSampleOrg(peerWtihOrg);
        client.setUserContext(sampleOrg.getPeerAdmin());
        for (String orderName : sampleOrg.getOrdererNames()) {
            Properties ordererProperties = config.getOrdererProperties(orderName);

            sampleOrg.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                    ordererProperties));
        }
        for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);

            Properties peerProperties = config.getPeerProperties(peerName);
            // properties
            // for
            // peer..
            // if
            // any.
            if (peerProperties == null) {
                peerProperties = new Properties();
            }
            // Example of setting specific options on grpc's NettyChannelBuilder
            // peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize",
            // 9000000);

            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);

            sampleOrg.addPeer(peer);
        }
    }

    private ChaincodeID getChaincodeId(String chaincodeName, String chainCodeVersion) {
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chainCodeVersion).setPath(chainCodePath + "/" + chaincodeName)
                .build();
        return chaincodeID;
    }


    @Override
    public synchronized String register(String name, String password, String peerWithOrg) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);

        Org sampleOrg = config.getSampleOrg(peerWithOrg);
        HFCAClient ca = sampleOrg.getCAClient();

        String orgName = sampleOrg.getName();
        String mspid = sampleOrg.getMSPID();
        ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
//            if (config.isRunningFabricTLS()) {
//                final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
//                enrollmentRequestTLS.addHost("localhost");
//                enrollmentRequestTLS.setProfile("tls");
//                final Enrollment enroll = ca.enroll("admin", "adminpw", enrollmentRequestTLS);
//                final String tlsCertPEM = enroll.getCert();
//                final String tlsKeyPEM = getPEMStringFromPrivateKey(enroll.getKey());
//
//                final Properties tlsProperties = new Properties();
//
//                tlsProperties.put("clientKeyBytes", tlsKeyPEM.getBytes(UTF_8));
//                tlsProperties.put("clientCertBytes", tlsCertPEM.getBytes(UTF_8));
//                clientTLSProperties.put(sampleOrg.getName(), tlsProperties);
//                //Save in samplestore for follow on tests.
//                sampleStore.storeClientPEMTLCertificate(sampleOrg, tlsCertPEM);
//                sampleStore.storeClientPEMTLSKey(sampleOrg, tlsKeyPEM);
//            }

        HFCAInfo info = ca.info(); //just check if we connect at all.
        String infoName = info.getCAName();
        logger.info("CAName: " + infoName);
//            if (infoName != null && !infoName.isEmpty()) {
//                //返回错误信息
//
//            }
        HyperUser admin = new HyperUser(adminName, orgName);


        admin.setEnrollment(ca.enroll(admin.getName(), adminPwd));
        admin.setMspId(mspid);

        HyperUser user = new HyperUser(name, orgName);

        RegistrationRequest rr = new RegistrationRequest(user.getName());
        rr.setSecret(password);

        try {
            user.setEnrollmentSecret(ca.register(rr, admin));
        } catch (RegistrationException re) {
            re.printStackTrace();
            logger.error(re.getMessage());
            throw re;
        }

        return "User " + name + " Registered Successfully";
    }


    public String loadUserFromPersistence(String name, String password, String peerWithOrg) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);


        Org sampleOrg = config.getSampleOrg(peerWithOrg);

        HFCAClient ca = sampleOrg.getCAClient();
        String orgName = sampleOrg.getName();
        String msPid = sampleOrg.getMSPID();
        ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        HyperUser admin = new HyperUser(adminName, orgName);
        admin.setEnrollment(ca.enroll(admin.getName(), adminPwd));
        admin.setMspId(msPid);

        sampleOrg.setAdmin(admin); // The admin of this org.


        // No need to enroll or register all done in End2endIt !
        HyperUser user = new HyperUser(name, orgName);

        //根据密码获取证书
        user.setEnrollment(ca.enroll(user.getName(), password));
        user.setMspId(msPid);
//        HFCAAffiliation aff = ca.getHFCAAffiliations(user);
//        Collection<HFCAIdentity> idlist = ca.getHFCAIdentities(user);
//            user.setEnrollment(ca.reenroll(user));

        sampleOrg.addUser(user); // Remember user belongs to this Org


        String sampleOrgName = sampleOrg.getName();
        String sampleOrgDomainName = sampleOrg.getDomainName();
        HyperUser peerOrgAdmin = new HyperUser(sampleOrgName + "Admin", orgName);
        peerOrgAdmin.setMspId(msPid);

        File certificateFile = Paths.get(config.getChannelPath(), "crypto-config/peerOrganizations/",
                sampleOrgDomainName, format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem",
                        sampleOrgDomainName, sampleOrgDomainName))
                .toFile();
        File privateKeyFile = ConnectionUtil.findFileSk(Paths.get(config.getChannelPath(),
                "crypto-config/peerOrganizations/", sampleOrgDomainName,
                format("/users/Admin@%s/msp/keystore", sampleOrgDomainName)).toFile());

        String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");

        PrivateKey privateKey = Utils.getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
        peerOrgAdmin.setEnrollment(new SampleStoreEnrollement(privateKey, certificate));

        sampleOrg.setPeerAdmin(peerOrgAdmin);

        return "Successfully loaded member from persistence";
    }

    @Override
    public String constructChannel(String channelName, String peerWithOrg) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);
        Org sampleOrg = config.getSampleOrg(peerWithOrg);
        logger.info("Constructing channel " + channelName);

        client.setUserContext(sampleOrg.getPeerAdmin());

        Collection<Orderer> orderers = new LinkedList<>();
        for (String orderName : sampleOrg.getOrdererNames()) {
            Properties ordererProperties = config.getOrdererProperties(orderName);

            orderers.add(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                    ordererProperties));
        }

        Orderer anOrderer = orderers.iterator().next();
        orderers.remove(anOrderer);
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(
                new File(PATH + "/artifacts/channel/channel-artifacts/" + channelName + ".tx"));

        // Create channel that has only one signer that is this orgs peer admin.
        // If channel creation policy needed more signature they would need to
        // be added too.

        Channel newChannel = client.newChannel(channelName, anOrderer, channelConfiguration,
                client.getChannelConfigurationSignature(channelConfiguration, sampleOrg.getPeerAdmin()));


        logger.info("Created channel " + channelName);
        for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);

            Properties peerProperties = config.getPeerProperties(peerName);
            // properties
            // for
            // peer..
            // if
            // any.
            if (peerProperties == null) {
                peerProperties = new Properties();
            }
            // Example of setting specific options on grpc's NettyChannelBuilder
            // peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize",
            // 9000000);

            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
            newChannel.joinPeer(peer, createPeerOptions());
            logger.info("Peer " + peerName + " joined channel " + channelName);
            sampleOrg.addPeer(peer);
        }

        for (Orderer orderer : orderers) { // add remaining orderers if any.
            newChannel.addOrderer(orderer);
        }

//        for (String eventHubName : sampleOrg.getEventHubNames()) {
//
//            final Properties eventHubProperties = config.getEventHubProperties(eventHubName);
//            EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
//                    eventHubProperties);
//            newChannel.addEventHub(eventHub);
//        }

        newChannel.initialize();

        logger.info("Finished initialization channel " + channelName);


        return "Channel created successfully";

    }

    private void waitOnFabric(int additional) {

    }


    @Override
    public String installChaincode(String name, String peerWithOrg, String channelName, String chaincodeName, String chainCodeVersion) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);

        loadOrderersAndPeers(client, peerWithOrg);
        Org sampleOrg = config.getSampleOrg(peerWithOrg);
        client.setUserContext(sampleOrg.getPeerAdmin());

        ChaincodeID chaincodeID = getChaincodeId(chaincodeName, chainCodeVersion);

        logger.info("Running channel " + channelName);

        logger.info("Creating install proposal");
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);
        installProposalRequest.setChaincodeSourceLocation(new File(PATH + "/artifacts/"));
        installProposalRequest.setChaincodeVersion(chainCodeVersion);
        installProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);
        installProposalRequest.setUserContext(sampleOrg.getPeerAdmin());
        logger.info("Sending install proposal");
        int numInstallProposal = 0;

        Collection<ProposalResponse> responses;
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        Collection<Peer> peers = sampleOrg.getPeers();
        numInstallProposal = numInstallProposal + peers.size();
        responses = client.sendInstallProposal(installProposalRequest, peers);

        for (ProposalResponse response : responses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                logger.info("Successful install proposal response Txid: " + response.getTransactionID() + " from peer " + response.getPeer().getName());
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        //   }
        logger.info("Received " + numInstallProposal + " install proposal responses. Successful+verified: " + successful.size() + " . Failed: " + failed.size());

        if (failed.size() > 0) {
            ProposalResponse first = failed.iterator().next();
            logger.error("Not enough endorsers for install :" + successful.size() + ".  " + first.getMessage());
            return "Not enough endorsers for install :" + first.getMessage();
        }


        return "Chaincode installed successfully";

    }

    @Override
    public String instantiateChaincode(String name, String peerWithOrg, String channelName, String chaincodeName, String chaincodeFunction, String[] chaincodeArgs, String chainCodeVersion) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);

        client.setUserContext(config.getSampleOrg(peerWithOrg).getPeerAdmin());

        ChaincodeID chaincodeID = getChaincodeId(chaincodeName, chainCodeVersion);
        Channel channel = reconstructChannel(peerWithOrg, channelName, client);

        logger.info("Running channel " + channelName);


        Collection<Orderer> orderers = channel.getOrderers();

        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(config.getProposalWaitTime());
        instantiateProposalRequest.setChaincodeID(chaincodeID);
        instantiateProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);
        instantiateProposalRequest.setFcn(chaincodeFunction);
        instantiateProposalRequest.setArgs(chaincodeArgs);
        instantiateProposalRequest.setChaincodeVersion(chainCodeVersion);
        instantiateProposalRequest.setUserContext(config.getSampleOrg(peerWithOrg).getPeerAdmin());

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm);

        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy
                .fromYamlFile(new File(PATH + "/artifacts/chaincodeendorsementpolicy.yaml"));
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        Collection<ProposalResponse> responses;
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        logger.info("Sending instantiateProposalRequest to all peers with arguments: " + chaincodeArgs);
        successful.clear();
        failed.clear();

        responses = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());

        for (ProposalResponse response : responses) {
            if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
                logger.info("Succesful instantiate proposal response Txid: " + response.getTransactionID() + " from peer " + response.getPeer().getName());
            } else {
                failed.add(response);
            }
        }
        logger.info("Received " + responses.size() + " instantiate proposal responses. Successful+verified: " + successful.size() + " . Failed: " + failed.size());
        if (failed.size() > 0) {
            for (ProposalResponse fail : failed) {

                logger.info("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with " + fail.getMessage() + ", on peer" + fail.getPeer());

            }
            ProposalResponse first = failed.iterator().next();
            logger.error("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with " + first.getMessage() + ". Was verified:" + first.isVerified());
            return "endorser failed";
        }
        logger.info("Sending instantiateTransaction to orderer ");
        logger.info("orderers" + orderers);
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
        String result = channel.sendTransaction(successful, orderers,config.getSampleOrg(peerWithOrg).getPeerAdmin()).thenApply(transactionEvent -> {
            waitOnFabric(0);
//            BlockEvent blockEvent = transactionEvent.getBlockEvent();

            logger.info("Finished instantiate transaction with transaction id " + transactionEvent.getTransactionID());
            return "Chaincode instantiated Successfully";
        }).exceptionally(e -> {
            e.printStackTrace();
            logger.info(" failed with " + e.getClass().getName() + " exception " + e.getMessage());
            return " failed with " + e.getClass().getName() + " exception " + e.getMessage();
        }).get(config.getTransactionWaitTime(), TimeUnit.SECONDS);

        return result;
    }

    @Override
    public String invokeChaincode(String name, String belongWithOrg, String[] peerWithOrgs, String channelName, String chaincodeName,
                                  String chaincodeFunction, String[] chaincodeArgs, String chainCodeVersion) throws Exception {

        HFClient client = HFClient.createNewInstance();
        checkConfig(client);

        client.setUserContext(config.getSampleOrg(belongWithOrg).getPeerAdmin());

        ChaincodeID chaincodeID = getChaincodeId(chaincodeName, chainCodeVersion);
        Channel channel = reconstructChannel(peerWithOrgs, channelName, client);

        logger.info("Running channel " + channelName);


        logger.debug("chaincodeFunction" + chaincodeFunction);
        logger.debug("chaincodeArgs" + chaincodeArgs);


        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setChaincodeLanguage(Type.GO_LANG);
        transactionProposalRequest.setFcn(chaincodeFunction);
        transactionProposalRequest.setProposalWaitTime(config.getProposalWaitTime());
        transactionProposalRequest.setArgs(chaincodeArgs);
        transactionProposalRequest.setChaincodeVersion(chainCodeVersion);
        transactionProposalRequest.setUserContext(config.getSampleOrg(belongWithOrg).getUser(name));

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

        logger.info("sending transactionProposal to all peers with arguments: " + chaincodeFunction + "," + chaincodeArgs);
        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                logger.info("Successful transaction proposal response Txid: " + response.getTransactionID() + " from peer " + response.getPeer().getName());
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        // Check that all the proposals are consistent with each other. We should have only one set
        // where all the proposals above are consistent. Note the when sending to Orderer this is done automatically.
        //  Shown here as an example that applications can invoke and select.
        // See org.hyperledger.fabric.sdk.proposal.consistency_validation config property.
        Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(transactionPropResp);
        if (proposalConsistencySets.size() != 1) {
            logger.error(format("Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size()));
        }

        logger.info("Received " + transactionPropResp.size() + " transaction proposal responses. Successful+verified: " + successful.size() + " . Failed: " + failed.size());
        if (failed.size() > 0) {
            ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
            logger.error("Not enough endorsers for invoke:" + failed.size() + " endorser error: " +
                    firstTransactionProposalResponse.getMessage() +
                    ". Was verified: " + firstTransactionProposalResponse.isVerified());
            return firstTransactionProposalResponse.getMessage();
        }
        logger.info("Successfully received transaction proposal responses.");
        ProposalResponse resp = successful.iterator().next();
        byte[] x = resp.getChaincodeActionResponsePayload(); // This is the data returned by the chaincode.
        String resultAsString = null;
        if (x != null) {
            resultAsString = new String(x, "UTF-8");
        }
        logger.debug("getChaincodeActionResponseReadWriteSetInfo:::"
                + resp.getChaincodeActionResponseReadWriteSetInfo());
        ChaincodeID cid = resp.getChaincodeID();

        ////////////////////////////
        // Send Transaction Transaction to orderer
        logger.info("Sending chaincode transaction " + chaincodeName + "_" + chaincodeFunction + " to orderer.");
        String result = channel.sendTransaction(successful,config.getSampleOrg(belongWithOrg).getUser(name)).thenApply(transactionEvent -> {

            waitOnFabric(0);

            logger.info("transaction event is valid " + transactionEvent.isValid()); // must
            for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo info : transactionEvent.getTransactionActionInfos()) {
                logger.info("*************" + info.getResponseMessage());
            }
            // be
            // valid
            // to
            // be
            // here.
            logger.info("Finished invoke transaction with transaction id " + transactionEvent.getTransactionID());

            return "Transaction invoked successfully";
        }).exceptionally(e -> {
            if (e instanceof TransactionEventException) {
                BlockEvent.TransactionEvent te = ((TransactionEventException) e).getTransactionEvent();
                if (te != null) {
                    logger.error(format("Transaction with txid " + te.getTransactionID() + " failed. " + e.getMessage()));
                }
            }

            logger.error("failed with " + e.getClass().getName() + " exception " + e.getMessage());
            return "failed with " + e.getClass().getName() + " exception " + e.getMessage();
        }).get(config.getTransactionWaitTime(), TimeUnit.SECONDS);
        logger.info("Transaction invoked " + result);

        return result;
    }

    @Override
    public String queryChaincode(String name, String peerWithOrg, String channelName, String chaincodeName, String chaincodeFunction, String[] chaincodeArgs, String chainCodeVersion) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);
        client.setUserContext(config.getSampleOrg(peerWithOrg).getPeerAdmin());

        ChaincodeID chaincodeID = getChaincodeId(chaincodeName, chainCodeVersion);
        Channel channel = reconstructChannel(peerWithOrg, channelName, client);

        logger.info("Running channel " + channelName);
        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(chaincodeArgs);
        queryByChaincodeRequest.setFcn(chaincodeFunction);
        queryByChaincodeRequest.setChaincodeID(chaincodeID);
        queryByChaincodeRequest.setChaincodeVersion(chainCodeVersion);
        queryByChaincodeRequest.setChaincodeLanguage(Type.GO_LANG);
        queryByChaincodeRequest.setUserContext(config.getSampleOrg(peerWithOrg).getUser(name));

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
        queryByChaincodeRequest.setTransientMap(tm2);


        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());
        for (ProposalResponse proposalResponse : queryProposals) {
            if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                logger.error("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " + proposalResponse.getStatus() +
                        ". Messages: " + proposalResponse.getMessage()
                        + ". Was verified : " + proposalResponse.isVerified());
            } else {
                String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                logger.info("Query payload of b from peer" + proposalResponse.getPeer().getName() + " returned " + payload);
                return payload;
            }
        }

        return "Caught an exception while quering chaincode";
    }

    @Override
    public BlockInfo blockchainInfo(String name, String peerWithOrg, String channelName) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);

        Org sampleOrg = config.getSampleOrg(peerWithOrg);
        client.setUserContext(sampleOrg.getUser(name));
        //Set<Peer> peerSet = sampleOrg.getPeers();

        Channel channel = reconstructChannel(peerWithOrg, channelName, client);

        BlockchainInfo channelInfo = channel.queryBlockchainInfo();
        logger.info("Channel info for : " + channelName);
        logger.info("Channel height: " + channelInfo.getHeight());
        String chainCurrentHash = Hex.encodeHexString(channelInfo.getCurrentBlockHash());
        String chainPreviousHash = Hex.encodeHexString(channelInfo.getPreviousBlockHash());
        logger.info("Chain current block hash: " + chainCurrentHash);
        logger.info("Chain previous block hash: " + chainPreviousHash);

        BlockInfo returnedBlock = channel.queryBlockByNumber(channelInfo.getHeight() - 1);
        String previousHash = Hex.encodeHexString(returnedBlock.getPreviousHash());
        logger.info("queryBlockByNumber returned correct block with blockNumber " + returnedBlock.getBlockNumber()
                + " \n previous_hash " + previousHash);

        byte[] hashQuery = returnedBlock.getPreviousHash();
        returnedBlock = channel.queryBlockByHash(hashQuery);
        logger.info("queryBlockByHash returned block with blockNumber " + returnedBlock.getBlockNumber());

        return returnedBlock;
    }

    @Override
    public String blockChainInfoByTxnId(String name, String peerWithOrg, String channelName,String txId) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);

        Org sampleOrg = config.getSampleOrg(peerWithOrg);
        client.setUserContext(sampleOrg.getUser(name));

        Channel channel = reconstructChannel(peerWithOrg, channelName, client);

        Map<String,Object> blockMap = new HashMap<>();
        BlockInfo blockInfo = channel.queryBlockByTransactionID(txId);
        blockMap.put("blockNumber",blockInfo.getBlockNumber());
        blockMap.put("dataHash", blockInfo.getDataHash());
        blockMap.put("previousHash",blockInfo.getPreviousHash());


        return JSONObject.toJSONString(blockMap);
    }

    @Override
    public String updateChaincode(String name, String peerWithOrg, String channelName, String chaincodeName, String chaincodeFunction, String[] chaincodeArgs, String chainCodeVersion) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);
        client.setUserContext(config.getSampleOrg(peerWithOrg).getPeerAdmin());
        ChaincodeID chaincodeID = getChaincodeId(chaincodeName, chainCodeVersion);
        Channel channel = reconstructChannel(peerWithOrg, channelName, client);

        logger.info("Running channel " + channelName);


        Collection<Orderer> orderers = channel.getOrderers();

        UpgradeProposalRequest upgradeProposalRequest = client.newUpgradeProposalRequest();
        upgradeProposalRequest.setProposalWaitTime(config.getProposalWaitTime());
        upgradeProposalRequest.setChaincodeID(chaincodeID);
        upgradeProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);
        upgradeProposalRequest.setFcn(chaincodeFunction);
        upgradeProposalRequest.setArgs(chaincodeArgs);
        upgradeProposalRequest.setChaincodeVersion(chainCodeVersion);
        upgradeProposalRequest.setUserContext(config.getSampleOrg(peerWithOrg).getPeerAdmin());

        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "UpgradeProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "UpgradeProposalRequest".getBytes(UTF_8));
        upgradeProposalRequest.setTransientMap(tm);

        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy
                .fromYamlFile(new File(PATH + "/artifacts/chaincodeendorsementpolicy.yaml"));
        upgradeProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        Collection<ProposalResponse> responses;
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();

        logger.info("Sending instantiateProposalRequest to all peers with arguments: " + chaincodeArgs);
        successful.clear();
        failed.clear();
        responses = channel.sendUpgradeProposal(upgradeProposalRequest, channel.getPeers());

        for (ProposalResponse response : responses) {
            if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
                logger.info("Succesful update proposal response Txid: " + response.getTransactionID() + " from peer " + response.getPeer().getName());
            } else {
                failed.add(response);
            }
        }
        logger.info("Received " + responses.size() + " update proposal responses. Successful+verified: " + successful.size() + " . Failed: " + failed.size());
        if (failed.size() > 0) {
            for (ProposalResponse fail : failed) {

                logger.info("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with " + fail.getMessage() + ", on peer" + fail.getPeer());

            }
            ProposalResponse first = failed.iterator().next();
            logger.error("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with " + first.getMessage() + ". Was verified:" + first.isVerified());
        }
        logger.info("Sending updateTransaction to orderer ");
        logger.info("orderers" + orderers);
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
        channel.sendTransaction(successful, orderers,config.getSampleOrg(peerWithOrg).getPeerAdmin()).thenApply(transactionEvent -> {
            waitOnFabric(0);
            BlockEvent blockEvent = transactionEvent.getBlockEvent();
            logger.info("Finished update transaction with transaction id " + transactionEvent.getTransactionID());
            return "Finished update transaction with transaction id " + transactionEvent.getTransactionID();
        }).exceptionally(e -> {
            e.printStackTrace();
            logger.info(" failed with " + e.getClass().getName() + " exception " + e.getMessage());
            return " failed with " + e.getClass().getName() + " exception " + e.getMessage();
        }).get(config.getTransactionWaitTime(), TimeUnit.SECONDS);

        return "Chaincode upgrade Successfully";
    }


    public Channel reconstructChannel(String[] peerWithOrgs, String channelName, HFClient client) throws Exception {

        try {

            Channel newChannel = client.newChannel(channelName);
            for (String peerWithOrg : peerWithOrgs) {
                loadOrderersAndPeers(client, peerWithOrg);
                Org sampleOrg = config.getSampleOrg(peerWithOrg);

                for (String orderName : sampleOrg.getOrdererNames()) {

                    newChannel.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                            config.getOrdererProperties(orderName)));
                }

                for (String peerName : sampleOrg.getPeerNames()) {
                    logger.debug(peerName);
                    String peerLocation = sampleOrg.getPeerLocation(peerName);
                    Peer peer = client.newPeer(peerName, peerLocation, config.getPeerProperties(peerName));

                    // Query the actual peer for which channels it belongs to and check
                    // it belongs to this channel
                    try {
                        Set<String> channels = client.queryChannels(peer);
                        if (!channels.contains(channelName)) {
                            logger.info("Peer " + peerName + " does not appear to belong to channel " + channelName);
                        }

                        newChannel.addPeer(peer);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                        continue;
                    }
                }

//                for (String eventHubName : sampleOrg.getEventHubNames()) {
//
//                    final Properties eventHubProperties = config.getEventHubProperties(eventHubName);
//                    EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
//                            eventHubProperties);
//                    newChannel.addEventHub(eventHub);
//                }
            }

            newChannel.initialize();

            return newChannel;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    public Channel reconstructChannel(String peerWithOrg, String channelName, HFClient client) throws Exception {

        try {

            Channel newChannel = client.newChannel(channelName);

            loadOrderersAndPeers(client, peerWithOrg);
            Org sampleOrg = config.getSampleOrg(peerWithOrg);

            for (String orderName : sampleOrg.getOrdererNames()) {

                newChannel.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                        config.getOrdererProperties(orderName)));
            }

            for (String peerName : sampleOrg.getPeerNames()) {
                logger.debug(peerName);
                String peerLocation = sampleOrg.getPeerLocation(peerName);
                Peer peer = client.newPeer(peerName, peerLocation, config.getPeerProperties(peerName));

                // Query the actual peer for which channels it belongs to and check
                // it belongs to this channel
                try {
                    Set<String> channels = client.queryChannels(peer);
                    if (!channels.contains(channelName)) {
                        logger.info("Peer " + peerName + " does not appear to belong to channel " + channelName);
                    }

                    newChannel.addPeer(peer);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    continue;
                }
            }

//                for (String eventHubName : sampleOrg.getEventHubNames()) {
//
//                    final Properties eventHubProperties = config.getEventHubProperties(eventHubName);
//                    EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
//                            eventHubProperties);
//                    newChannel.addEventHub(eventHub);
//                }


            newChannel.initialize();

            return newChannel;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public String joinChannel(String channelName, String peerWithOrg) throws Exception {
        HFClient client = HFClient.createNewInstance();
        checkConfig(client);
        client.setUserContext(config.getSampleOrg(peerWithOrg).getPeerAdmin());
        Channel newChannel = client.newChannel(channelName);


        loadOrderersAndPeers(client, peerWithOrg);
        Org sampleOrg = config.getSampleOrg(peerWithOrg);


        for (String orderName : sampleOrg.getOrdererNames()) {

            newChannel.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                    config.getOrdererProperties(orderName)));
        }

        for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);

            Properties peerProperties = config.getPeerProperties(peerName);

            if (peerProperties == null) {
                peerProperties = new Properties();
            }


            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
            Set<String> channels = client.queryChannels(peer);
            if (!channels.contains(channelName)) {
                newChannel.joinPeer(peer, createPeerOptions());
            } else {
                logger.info("Peer " + peerName + "already joined channel " + channelName);
            }

            logger.info("Peer " + peerName + " joined channel " + channelName);
            sampleOrg.addPeer(peer);
        }

//            for (String eventHubName : sampleOrg.getEventHubNames()) {
//                EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
//                        config.getEventHubProperties(eventHubName));
//                newChannel.addEventHub(eventHub);
//            }


        newChannel.initialize();


        logger.info("Finished joined channel" + channelName);

        return "Channel joined successfully";

    }
}
