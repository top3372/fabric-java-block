package com.ideal.blockchain.service.block;

import com.alibaba.fastjson.JSONObject;
import com.ideal.blockchain.config.ChannelContext;
import com.ideal.blockchain.config.Config;
import com.ideal.blockchain.config.HyperledgerConfiguration;
import com.ideal.blockchain.model.Org;
import com.ideal.blockchain.service.FabricCaUserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import static org.hyperledger.fabric.sdk.Channel.PeerOptions.createPeerOptions;

/**
 * @author: LeonMa
 * @date: 2019/01/10 12:01
 */
@Slf4j
@Service
public class ChannelService {

    @Autowired
    private HyperledgerConfiguration hyperledgerConfiguration;


    public String constructChannel(String channelName, String peerWithOrg) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);
        Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);
        log.info("Constructing channel " + channelName);

        client.setUserContext(sampleOrg.getPeerAdmin());

        Collection<Orderer> orderers = new LinkedList<>();
        for (String orderName : sampleOrg.getOrdererNames()) {
            Properties ordererProperties = HyperledgerConfiguration.config.getOrdererProperties(orderName);

            orderers.add(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                    ordererProperties));
        }

        Orderer anOrderer = orderers.iterator().next();
        orderers.remove(anOrderer);
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(
                new File(HyperledgerConfiguration.PATH + "/artifacts/channel/channel-artifacts/" + channelName + ".tx"));

        // Create channel that has only one signer that is this orgs peer admin.
        // If channel creation policy needed more signature they would need to
        // be added too.

        Channel newChannel = client.newChannel(channelName, anOrderer, channelConfiguration,
                client.getChannelConfigurationSignature(channelConfiguration, sampleOrg.getPeerAdmin()));


        log.info("Created channel " + channelName);
        for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);

            Properties peerProperties = HyperledgerConfiguration.config.getPeerProperties(peerName);

            if (peerProperties == null) {
                peerProperties = new Properties();
            }


            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
            newChannel.joinPeer(peer, createPeerOptions());
            log.info("Peer " + peerName + " joined channel " + channelName);
            sampleOrg.addPeer(peer);
        }

        for (Orderer orderer : orderers) {
            // add remaining orderers if any.
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
        Config.channelMap.put(channelName + peerWithOrg,newChannel);
        log.info("Finished initialization channel " + channelName);


        return "Channel created successfully";

    }



    public void reconstructChannel(String[] peerWithOrgs, String channelName, HFClient client) throws Exception {
        peerWithOrgs = StringUtils.sortStringArray(peerWithOrgs);
        try {

            Channel newChannel = Config.channelMap.get(channelName+ JSONObject.toJSONString(peerWithOrgs));
            if(newChannel == null) {
                newChannel = client.newChannel(channelName);
                for (String peerWithOrg : peerWithOrgs) {
                    hyperledgerConfiguration.loadOrderersAndPeers(client, peerWithOrg);
                    Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);

                    for (String orderName : sampleOrg.getOrdererNames()) {

                        newChannel.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                                HyperledgerConfiguration.config.getOrdererProperties(orderName)));
                    }

                    for (String peerName : sampleOrg.getPeerNames()) {
                        log.debug(peerName);
                        //将机构下面的背书peer加入
                        String peerLocation = sampleOrg.getPeerLocation(peerName);
                        Peer peer = client.newPeer(peerName, peerLocation, HyperledgerConfiguration.config.getPeerProperties(peerName));

                        try {
                            Set<String> channels = client.queryChannels(peer);
                            if (!channels.contains(channelName)) {
                                log.info("Peer " + peerName + " does not appear to belong to channel " + channelName);
                            }
                            newChannel.addPeer(peer);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error(e.getMessage());
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
                Config.channelMap.put(channelName+JSONObject.toJSONString(peerWithOrgs),newChannel);
                ChannelContext.set(newChannel);
            }else{
                ChannelContext.set(newChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public void reconstructChannel(String peerWithOrg, String channelName, HFClient client) throws Exception {

        try {
            Channel newChannel = Config.channelMap.get(channelName+peerWithOrg);
            if(newChannel == null) {
                newChannel = client.newChannel(channelName);

                hyperledgerConfiguration.loadOrderersAndPeers(client, peerWithOrg);
                Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);

                for (String orderName : sampleOrg.getOrdererNames()) {

                    newChannel.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                            HyperledgerConfiguration.config.getOrdererProperties(orderName)));
                }

                for (String peerName : sampleOrg.getPeerNames()) {
                    log.debug(peerName);
                    String peerLocation = sampleOrg.getPeerLocation(peerName);
                    Peer peer = client.newPeer(peerName, peerLocation, HyperledgerConfiguration.config.getPeerProperties(peerName));

                    // Query the actual peer for which channels it belongs to and check
                    // it belongs to this channel
//                try {
//                    Set<String> channels = client.queryChannels(peer);
//                    if (!channels.contains(channelName)) {
//                        log.info("Peer " + peerName + " does not appear to belong to channel " + channelName);
//                    }

                    newChannel.addPeer(peer);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    log.error(e.getMessage());
//                    continue;
//                }
                }

//                for (String eventHubName : sampleOrg.getEventHubNames()) {
//
//                    final Properties eventHubProperties = config.getEventHubProperties(eventHubName);
//                    EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
//                            eventHubProperties);
//                    newChannel.addEventHub(eventHub);
//                }


                newChannel.initialize();
                Config.channelMap.put(channelName+peerWithOrg,newChannel);
                ChannelContext.set(newChannel);
            }else{
                ChannelContext.set(newChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }


    public String joinChannel(String channelName, String peerWithOrg) throws Exception {
        HFClient client = HFClient.createNewInstance();
        hyperledgerConfiguration.checkConfig(client);
        client.setUserContext(HyperledgerConfiguration.config.getSampleOrg(peerWithOrg).getPeerAdmin());
        Channel newChannel = client.newChannel(channelName);


        hyperledgerConfiguration.loadOrderersAndPeers(client, peerWithOrg);
        Org sampleOrg = HyperledgerConfiguration.config.getSampleOrg(peerWithOrg);


        for (String orderName : sampleOrg.getOrdererNames()) {

            newChannel.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                    HyperledgerConfiguration.config.getOrdererProperties(orderName)));
        }

        for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);

            Properties peerProperties = HyperledgerConfiguration.config.getPeerProperties(peerName);

            if (peerProperties == null) {
                peerProperties = new Properties();
            }


            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
            Set<String> channels = client.queryChannels(peer);
            if (!channels.contains(channelName)) {
                newChannel.joinPeer(peer, createPeerOptions());
            } else {
                log.info("Peer " + peerName + "already joined channel " + channelName);
            }

            log.info("Peer " + peerName + " joined channel " + channelName);
            sampleOrg.addPeer(peer);
        }

//            for (String eventHubName : sampleOrg.getEventHubNames()) {
//                EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
//                        config.getEventHubProperties(eventHubName));
//                newChannel.addEventHub(eventHub);
//            }


        newChannel.initialize();
        Config.channelMap.put(channelName + peerWithOrg,newChannel);

        log.info("Finished joined channel" + channelName);

        return "Channel joined successfully";

    }
}
