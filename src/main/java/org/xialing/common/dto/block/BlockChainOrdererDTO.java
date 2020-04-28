package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;


@Data
public class BlockChainOrdererDTO {

    private Long id;


    private String ordererCode;


    private String ordererName;


    private String ordererKey;


    private String ordererUrl;


    private String blockChainNetCode;


    private String state;


    private String description;


    private String remark;


    private String createBy;


    private Date createTime;


    private String updateBy;


    private Date updateTime;


    private String ordererCert;


}