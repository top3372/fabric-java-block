package org.xialing.common.dto.route.blockinfo;

import org.xialing.common.dto.route.base.Block;
import lombok.Data;

import java.util.List;

/**
 * @author leon
 * @version 1.0
 * @date 2019/11/11 11:35
 */
@Data
public class BlockList {
    private Long totalCount;

    private List<Block> blockList;

    private String requestId;

}
