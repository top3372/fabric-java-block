package org.xialing.common.dto.route.base;

import lombok.Data;

/**
 * @author leon
 * @version 1.0
 * @date 2019/11/11 11:42
 */
@Data
public class Block {
    private Long blockNum;
    private String dataHash;
    private Long blockId;
    private String preHash;
    private Long txCount;
}
