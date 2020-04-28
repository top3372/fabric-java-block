package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;


@Data
public class BlockChainChannelDTO {

    private Long id;


    private String channelCode;


    private String channelName;


    private String channelKey;


    private String blockChainNetCode;


    private String channelType;


    private String state;


    private String description;


    private String remark;


    private String createBy;


    private Date createTime;


    private String updateBy;


    private Date updateTime;

}