package org.xialing.common.dto.route.event;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Table: ROUTE_BLOCK_EVENT_MSG
 */
@Data
public class BlockEventMsgDTO implements Serializable {

    private Long id;


    private String eventMsgCode;


    private String eventCode;


    private String eventType;


    private String eventPayLoad;

    private String status;


    private String txId;


    private Long blockNo;


    private String funCode;


    private String contractCode;


    private String channelCode;


    private String blockChainNetCode;


    private String leagueCode;


    private Date createTime;

    private static final long serialVersionUID = 1L;
}