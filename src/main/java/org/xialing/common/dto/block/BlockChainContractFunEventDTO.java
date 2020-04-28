package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;


@Data
public class BlockChainContractFunEventDTO {

    private Long id;

    private String eventCode;

    private String eventName;

    private String eventKey;

    private String eventType;

    private String funCode;

    private String contractCode;

    private String state;

    private String description;

    private String remark;

    private String createBy;


    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}