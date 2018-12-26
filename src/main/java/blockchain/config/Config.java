/*
 *  Copyright 2016, 2017,2018, Mindtree Ltd., IBM, DTCC, Fujitsu Australia Software Technology, IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package blockchain.config;

import blockchain.model.Org;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.helper.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Config allows for a global config of the toolkit. Central location for all
 * toolkit configuration defaults.
 */

public class Config {

	private static final Log logger = LogFactory.getLog(Config.class);

	private static final String PROPBASE = "config.";
	private static String PATH = System.getProperty("user.dir");
	

	private static final String GOSSIPWAITTIME = PROPBASE + "GossipWaitTime";
	private static final String INVOKEWAITTIME = PROPBASE + "InvokeWaitTime";
	private static final String DEPLOYWAITTIME = PROPBASE + "DeployWaitTime";
	private static final String PROPOSALWAITTIME = PROPBASE + "ProposalWaitTime";

	private static final String ORGS = PROPBASE + "property.";
	private static final Pattern orgPat = Pattern.compile("^" + Pattern.quote(ORGS) + "([^\\.]+)\\.mspid$");

	private static final String BLOCKCHAINTLS = PROPBASE + "blockchain.tls";

	private static Config config;
	public static final Properties sdkProperties = new Properties();
	private final boolean runningTLS;
	private final boolean runningFabricCATLS;
	private final boolean runningFabricTLS;
	private static final HashMap<String, Org> sampleOrgs = new HashMap<>();

	public boolean isRunningFabricTLS() {
		return runningFabricTLS;
	}

	private Config() {

		try {

			/**
			 * All the properties will be obtained from config.properties file
			 */

			sdkProperties.load(new FileInputStream(PATH + "/config.properties"));

			

		} catch (IOException e) {
			// if not there no worries just use defaults
			logger.warn("Failed to load any configuration");
		} finally {

			// Default values
//
//			defaultProperty(GOSSIPWAITTIME, "5000");
//			defaultProperty(INVOKEWAITTIME, "100000");
//			defaultProperty(DEPLOYWAITTIME, "120000");
//			defaultProperty(PROPOSALWAITTIME, "120000");
//
//			defaultProperty(ORGS + "peerOrg1.mspid", "Org1MSP");
//			defaultProperty(ORGS + "peerOrg1.domname", "org1.example.com");
//			defaultProperty(ORGS + "peerOrg1.ca_location", "http://localhost:7054");
//			defaultProperty(ORGS + "peerOrg1.peer_locations",
//					"peer0.org1.example.com@grpc://localhost:7051, peer1.org1.example.com@grpc://localhost:7056");
//			defaultProperty(ORGS + "peerOrg1.orderer_locations", "orderer.example.com@grpc://localhost:7050");
//			defaultProperty(ORGS + "peerOrg1.eventhub_locations",
//					"peer0.org1.example.com@grpc://localhost:7053,peer1.org1.example.com@grpc://localhost:7058");
//
//			defaultProperty(BLOCKCHAINTLS, null);
			runningTLS = null != sdkProperties.getProperty(BLOCKCHAINTLS, null);
			runningFabricCATLS = runningTLS;
			runningFabricTLS = runningTLS;

			for (Map.Entry<Object, Object> x : sdkProperties.entrySet()) {
				final String key = x.getKey() + "";
				final String val = x.getValue() + "";

				if (key.startsWith(ORGS)) {

					Matcher match = orgPat.matcher(key);

					if (match.matches() && match.groupCount() == 1) {
						String orgName = match.group(1).trim();
						sampleOrgs.put(orgName, new Org(orgName, val.trim()));

					}
				}
			}

			for (Map.Entry<String, Org> org : sampleOrgs.entrySet()) {
				final Org sampleOrg = org.getValue();
				final String orgName = org.getKey();

				String peerNames = sdkProperties.getProperty(ORGS + orgName + ".peer_locations");
				String[] ps = peerNames.split("[ \t]*,[ \t]*");
				for (String peer : ps) {
					String[] nl = peer.split("[ \t]*@[ \t]*");
					sampleOrg.addPeerLocation(nl[0], grpcTLSify(nl[1]));
				}

				final String domainName = sdkProperties.getProperty(ORGS + orgName + ".domname");

				sampleOrg.setDomainName(domainName);

				String ordererNames = sdkProperties.getProperty(ORGS + orgName + ".orderer_locations");
				ps = ordererNames.split("[ \t]*,[ \t]*");
				for (String peer : ps) {
					String[] nl = peer.split("[ \t]*@[ \t]*");
					sampleOrg.addOrdererLocation(nl[0], grpcTLSify(nl[1]));
				}

//				String eventHubNames = sdkProperties.getProperty(ORGS + orgName + ".eventhub_locations");
//				ps = eventHubNames.split("[ \t]*,[ \t]*");
//				for (String peer : ps) {
//					String[] nl = peer.split("[ \t]*@[ \t]*");
//					sampleOrg.addEventHubLocation(nl[0], grpcTLSify(nl[1]));
//				}

				sampleOrg.setCALocation(httpTLSify(sdkProperties.getProperty((ORGS + org.getKey() + ".ca_location"))));

				if (true) {
					String cert = "artifacts/channel/crypto-config/peerOrganizations/DNAME/ca/ca.DNAME-cert.pem"
							.replaceAll("DNAME", domainName);
					File cf = new File(cert);
					if (!cf.exists() || !cf.isFile()) {
						throw new RuntimeException(" missing cert file " + cf.getAbsolutePath());
					}
					Properties properties = new Properties();
					properties.setProperty("pemFile", cf.getAbsolutePath());

					properties.setProperty("allowAllHostNames", "true");

					sampleOrg.setCAProperties(properties);

				}
			}

		}

	}

	private String grpcTLSify(String location) {
		location = location.trim();
		Exception e = Utils.checkGrpcUrl(location);
		if (e != null) {
			throw new RuntimeException(String.format("Bad  parameters for grpc url %s", location), e);
		}
		return runningFabricTLS ? location.replaceFirst("^grpc://", "grpcs://") : location;

	}

	private String httpTLSify(String location) {
		location = location.trim();

		return runningFabricCATLS ? location.replaceFirst("^http://", "https://") : location;
	}

	/**
	 * getConfig return back singleton for SDK configuration.
	 *
	 * @return Global configuration
	 */
	public static Config getConfig() {
		if (null == config) {
			config = new Config();
		}
		return config;

	}

	/**
	 * getProperty return back property for the given value.
	 *
	 * @param property
	 * @return String value for the property
	 */
	private String getProperty(String property) {

		String ret = sdkProperties.getProperty(property);

		if (null == ret) {
			logger.warn(String.format("No configuration value found for '%s'", property));
		}
		return ret;
	}

//	private static void defaultProperty(String key, String value) {
//
//		String ret = System.getProperty(key);
//		if (ret != null) {
//			sdkProperties.put(key, ret);
//		} else {
//			String envKey = key.toUpperCase().replaceAll("\\.", "_");
//			ret = System.getenv(envKey);
//			if (null != ret) {
//				sdkProperties.put(key, ret);
//			} else {
//				if (null == sdkProperties.getProperty(key) && value != null) {
//					sdkProperties.put(key, value);
//				}
//
//			}
//
//		}
//	}

	public int getTransactionWaitTime() {
		return Integer.parseInt(getProperty(INVOKEWAITTIME));
	}

	public int getDeployWaitTime() {
		return Integer.parseInt(getProperty(DEPLOYWAITTIME));
	}

	public int getGossipWaitTime() {
		return Integer.parseInt(getProperty(GOSSIPWAITTIME));
	}

	public long getProposalWaitTime() {
		return Integer.parseInt(getProperty(PROPOSALWAITTIME));
	}

	public Collection<Org> getSampleOrgs() {
		return Collections.unmodifiableCollection(sampleOrgs.values());
	}

	public Org getSampleOrg(String name) {
		return sampleOrgs.get(name);

	}

	public Properties getPeerProperties(String name) {

		return getEndPointProperties("peer", name);

	}

	public Properties getOrdererProperties(String name) {

		return getEndPointProperties("orderer", name);

	}

	private Properties getEndPointProperties(final String type, final String name) {

		final String domainName = getDomainName(name);

		File cert = Paths.get(getChannelPath(), "crypto-config/ordererOrganizations".replace("orderer", type),
				domainName, type + "s", name, "tls/server.crt").toFile();
		if (!cert.exists()) {
			throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", name,
					cert.getAbsolutePath()));
		}

		Properties ret = new Properties();
		ret.setProperty("pemFile", cert.getAbsolutePath());

		ret.setProperty("hostnameOverride", name);
		ret.setProperty("sslProvider", "openSSL");
		ret.setProperty("negotiationType", "TLS");

		return ret;
	}

	public Properties getEventHubProperties(String name) {

		return getEndPointProperties("peer", name); // uses same as named peer

	}

	public String getChannelPath() {

//		/**
//		 * for loading properties from hyperledger.properties file
//		 */
//		Properties hyperproperties = new Properties();
//		try {
//			hyperproperties.load(new FileInputStream("src/main/resources/hyperledger.properties"));
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		return PATH+"/artifacts/channel";

	}

	private String getDomainName(final String name) {
		int dot = name.indexOf(".");
		if (-1 == dot) {
			return null;
		} else {
			return name.substring(dot + 1);
		}

	}

}
