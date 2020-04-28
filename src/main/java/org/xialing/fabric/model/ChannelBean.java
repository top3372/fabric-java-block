package org.xialing.fabric.model;

import lombok.Builder;
import lombok.Data;
import lombok.Synchronized;
import org.hyperledger.fabric.sdk.Channel;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/8 23:29
 */
@Data
public class ChannelBean {

    private Channel channel;

    private int contractEventCount = 0;

}
