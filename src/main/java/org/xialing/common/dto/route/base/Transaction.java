package org.xialing.common.dto.route.base;

import lombok.Data;

/**
 * @author leon
 * @version 1.0
 * @date 2019/11/11 14:09
 */
@Data
public class Transaction {
    private String transactionId; //交易ID
    private String transactionHash; //交易hash
    private String createOrgName; //创建交易的组织名
    private Long blockId; //交易所在区块号
    private String transactionType; //交易类型（普通交易和配置交易）
    private String createTime; //交易创建时间
    private Long blockHeight; //交易所在区块高度
    private String transactionStatus; //交易状态
}
