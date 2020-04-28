package org.xialing.common.dto.route.blockinfo;

import org.xialing.common.dto.route.base.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author leon
 * @version 1.0
 * @date 2019/11/11 11:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BlockInfoRequest extends BaseRequest {

    private String baasRouteCode;

    private Long offset;

    private Long limit;

    private Long latestBlockNumber = 5L;
}
