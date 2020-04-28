package org.xialing.common.dto.route.query;

import lombok.Data;

/**
 * @author leon
 * @version 1.0
 * @date 2019/10/2 22:58
 */
@Data
public class QueryResult {

    private String requestSerialNo;
    private String orderNo;
    private String[] data;
    private String requestId;
    private String status;
    private String errorMsg;
    private String requestContext;

    private String responseContext;

}
