package org.xialing.common.dto.route.query;

import org.xialing.common.dto.route.base.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author leon
 * @version 1.0
 * @date 2019/10/2 22:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryRequest extends BaseRequest {

    private String requestSerialNo;             // 请求流水号
    private String baasRouteCode;               // 区块链渠道号(TBAAS)
    private String orderNo;                     // 链订单流水号
    private String[] args;                      // 合约参数


}
