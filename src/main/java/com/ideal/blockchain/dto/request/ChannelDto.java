package com.ideal.blockchain.dto.request;

public class ChannelDto extends BaseDto{



    private String channelName;

    private String peerWithOrg ;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getPeerWithOrg() {
        return peerWithOrg;
    }

    public void setPeerWithOrg(String peerWithOrg) {
        this.peerWithOrg = peerWithOrg;
    }


}
