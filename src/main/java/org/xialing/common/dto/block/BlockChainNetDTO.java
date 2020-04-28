package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;


@Data
public class BlockChainNetDTO {

    private Long id;


    private String blockChainNetCode;


    private String blockChainNetName;


    private String blockChainNetKey;


    private String blockChainNetType;


    private String consensusType;


    private String state;


    private String description;


    private String remark;


    private String createBy;


    private Date createTime;


    private String updateBy;


    private Date updateTime;


}