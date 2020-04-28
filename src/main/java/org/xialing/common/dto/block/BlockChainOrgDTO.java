package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class BlockChainOrgDTO {

    private Long id;

    private String orgCode;

    private String orgName;

    private String orgKey;

    private String orgPrivateKey;

    private String orgSignedCert;

    private String mspId;

    private String type;

    private String blockChainNetCode;

    private String state;

    private String description;

    private String remark;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private BlockChainOrgCaDTO blockChainOrgCa;

    private BlockChainOrgUserDTO blockChainOrgUser;

    private List<BlockChainOrgNodeDTO> nodeList;

}