package org.xialing.common.dto.route.base.request;

import org.xialing.common.dto.route.BaasRouteDTO;
import org.xialing.common.dto.block.*;
import lombok.Data;

import java.util.List;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/1 15:59
 */
@Data
public class BaseRequest {

    private BaasRouteDTO baasRouteDTO;      // BaaS连接相关信息(对应ROUTE_BAAS_ROUTE表)

    private BlockChainNetDTO blockChainNetDTO;

    private BlockChainChannelDTO blockChainChannelDTO;

    private List<BlockChainOrdererDTO> blockChainOrdererDTOs;

    private BlockChainContractDTO blockChainContractDTO;

    private BlockChainContractFunDTO blockChainContractFunDTO;

    private BlockChainOrgDTO currentBlockChainOrgDTO;

    private BlockChainOrgCaDTO currentBlockChainOrgCaDTO;

    private BlockChainOrgUserDTO currentBlockChainOrgUserDTO;

    private List<BlockChainOrgDTO> strategyOrgList;
}
