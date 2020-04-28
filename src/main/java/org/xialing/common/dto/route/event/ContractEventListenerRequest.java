package org.xialing.common.dto.route.event;

import org.xialing.common.dto.route.base.request.BaseRequest;
import org.xialing.common.dto.block.BlockChainContractFunDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/6 13:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractEventListenerRequest extends BaseRequest {


        private String leagueCode;

        private List<BlockChainContractFunDTO> funList;

}
