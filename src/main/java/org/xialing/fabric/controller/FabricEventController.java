package org.xialing.fabric.controller;

import org.xialing.common.dto.R;
import org.xialing.common.dto.route.event.ContractEventListenerRequest;
import org.xialing.common.enums.ResponseCodeEnum;
import org.xialing.fabric.service.FabricEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/5 16:33
 */
@RestController
@Slf4j
@RequestMapping("fabric-event")
public class FabricEventController {

    @Resource
    private FabricEventService fabricEventService;

    @GetMapping("register-contract-listener")
    public R registerContractListener(@RequestBody ContractEventListenerRequest contractEventListenerRequest){
        try {
            fabricEventService.registerContractListener(contractEventListenerRequest);
            return R.success();
        }catch (Exception e){
            log.error(e.getMessage());
            return R.error(ResponseCodeEnum.BUSI_ERROR.getCode(),e.getMessage());
        }
    }

    @GetMapping("unRegister-contract-listener")
    public R unRegisterContractListener(@RequestBody ContractEventListenerRequest contractEventListenerRequest){
        try {
            fabricEventService.unRegisterContractListener(contractEventListenerRequest);
            return R.success();
        }catch (Exception e){
            log.error(e.getMessage());
            return R.error(ResponseCodeEnum.BUSI_ERROR.getCode(),e.getMessage());
        }
    }

}
