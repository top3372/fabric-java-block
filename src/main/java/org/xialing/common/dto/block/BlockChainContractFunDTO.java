package org.xialing.common.dto.block;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class BlockChainContractFunDTO {

    private Long id;


    private String funCode;


    private String funName;


    private String funKey;


    private String funType;

    private String asyncFlag;

    private String businessCode;


    private String contractCode;


    private String state;


    private String description;


    private String remark;


    private String createBy;


    private Date createTime;


    private String updateBy;


    private Date updateTime;

    private String baasRouteCode;

    private List<BlockChainContractFunEventDTO> eventDTOList;

}