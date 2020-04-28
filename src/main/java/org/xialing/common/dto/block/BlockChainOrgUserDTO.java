package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;

@Data
public class BlockChainOrgUserDTO {

    private Long id;


    private String orgUserCode;


    private String orgUserName;

    private String orgCode;

    private String state;


    private String description;


    private String remark;


    private String createBy;


    private Date createTime;


    private String updateBy;


    private Date updateTime;


    private String orgUserKey;

    private String orgUserCert;

}