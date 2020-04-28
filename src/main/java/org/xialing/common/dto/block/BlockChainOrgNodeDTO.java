package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;

@Data
public class BlockChainOrgNodeDTO {

    private Long id;

    private String nodeCode;

    private String nodeName;

    private String nodeKey;

    private String nodeUrl;

    private String nodeType;

    private String orgCode;

    private String state;

    private String description;

    private String remark;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String nodeCert;

    private static final long serialVersionUID = 1L;
}