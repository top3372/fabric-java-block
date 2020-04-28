package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;

@Data
public class BlockChainContractDTO {

    private Long id;

    private String contractCode;

    private String contractName;

    private String contractKey;

    private String businessType;

    private String contractType;

    private String version;

    private String state;

    private String description;

    private String remark;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

}