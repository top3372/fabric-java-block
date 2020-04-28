package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;

@Data
public class BlockChainOrgCaDTO {

    private Long id;

    private String caCode;

    private String caName;

    private String caKey;

    private String caUrl;

    private String adminUserName;

    private String adminUserPwd;

    private String orgCode;

    private String state;

    private String description;

    private String remark;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String caCert;

}