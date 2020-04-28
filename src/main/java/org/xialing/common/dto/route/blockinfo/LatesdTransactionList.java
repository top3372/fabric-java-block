package org.xialing.common.dto.route.blockinfo;

import org.xialing.common.dto.route.base.Transaction;
import lombok.Data;

import java.util.List;

/**
 * @author leon
 * @version 1.0
 * @date 2019/11/11 11:34
 */
@Data
public class LatesdTransactionList {

    private Long totalCount;
    private List<Transaction> transactionList;

    private String requestId;

}
