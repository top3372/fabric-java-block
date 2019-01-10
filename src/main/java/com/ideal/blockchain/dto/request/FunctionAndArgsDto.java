/*
 *  Copyright 2018, Mindtree Ltd. - All Rights Reserved. 
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
package com.ideal.blockchain.dto.request;

/**
 * @author SWATI RAJ
 *
 */
public class FunctionAndArgsDto extends BaseDto{


	private String belongWithOrg;

	private String[] peerWithOrgs ;

	private String channelName;

	private String chainCodeVersion;
	private String chainCodeName;
	private String function;
	private String[] args;

	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public String getBelongWithOrg() {
		return belongWithOrg;
	}

	public void setBelongWithOrg(String belongWithOrg) {
		this.belongWithOrg = belongWithOrg;
	}

	public String[] getPeerWithOrgs() {
		return peerWithOrgs;
	}

	public void setPeerWithOrgs(String[] peerWithOrgs) {
		this.peerWithOrgs = peerWithOrgs;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChainCodeVersion() {
		return chainCodeVersion;
	}

	public void setChainCodeVersion(String chainCodeVersion) {
		this.chainCodeVersion = chainCodeVersion;
	}

	public String getChainCodeName() {
		return chainCodeName;
	}

	public void setChainCodeName(String chainCodeName) {
		this.chainCodeName = chainCodeName;
	}
}
