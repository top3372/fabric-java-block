package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NodeChannelDTO {

    private Long id;

    private String nodeCode;

    private String channelCode;

    private String orgCode;

    private String blockChainNetCode;

    private String state;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private List<String> channelCodes;

}
