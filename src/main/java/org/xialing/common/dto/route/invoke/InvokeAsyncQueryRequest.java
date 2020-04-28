package org.xialing.common.dto.route.invoke;

import org.xialing.common.dto.route.base.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author leon
 * @version 1.0
 * @date 2019/10/4 23:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InvokeAsyncQueryRequest extends BaseRequest {

    private String txId;                    // BaaS渠道返回单号(Tx_Id)
    private String orderNo;                 // 链订单流水号
    private String baasRouteCode;               // 区块链渠道号(TBAAS)

}
