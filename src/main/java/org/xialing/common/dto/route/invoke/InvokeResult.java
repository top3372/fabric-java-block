package org.xialing.common.dto.route.invoke;

import lombok.Data;

/**
 * @author leon
 * @version 1.0
 * @date 2019/10/2 22:57
 */
@Data
public class InvokeResult {

//    private String requestSerialNo;

    /**
     * 链订单流水号
     */
    private String orderNo;

    /**
     * 上链操作请求原文
     */
    private String requestContext;

    /**
     * 上链操作响应原文
     */
    private String responseContext;

    /**
     * 获取唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
     */
    private String requestId;

    /**
     * 获取交易ID 同 txId
     */
    private String returnNo;

    /**
     * 获取交易ID 同 returnNo
     */
    private String txId;

    /**
     * 请求状态：000001.请求已受理；000002.请求受理失败
     */
    private String status;

    /**
     * 上链错误信息
     */
    private String errorMsg;

    private Long blockNo;

}
