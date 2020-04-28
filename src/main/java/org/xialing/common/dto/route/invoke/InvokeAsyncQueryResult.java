package org.xialing.common.dto.route.invoke;

import lombok.Data;

/**
 * @author leon
 * @version 1.0
 * @date 2019/10/2 23:02
 */
@Data
public class InvokeAsyncQueryResult {

//    private String requestSerialNo;
    private String txValidationCode;    // 获取交易执行状态码
    private String txValidationMsg;     // 获取交易执行消息
    private Long blockId;               // 获取交易所在区块ID
    private String requestId;           // 获取唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
    private String orderNo;             // 链订单流水号
    private String returnNo;            // BaaS渠道返回单号(Tx_Id)
    private String status;              // 状态：000000.请求处理成功; 000002.请求受理失败;
    private String errorMsg;            // 获取交易执行消息
    private String requestContext;      // 链查询请求原文
    private String responseContext;     // 链查询响应原文

}
