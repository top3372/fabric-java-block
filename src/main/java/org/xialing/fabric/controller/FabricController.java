package org.xialing.fabric.controller;

import org.xialing.common.dto.R;

import org.xialing.common.dto.route.invoke.InvokeAsyncQueryRequest;
import org.xialing.common.dto.route.invoke.InvokeAsyncQueryResult;
import org.xialing.common.dto.route.invoke.InvokeRequest;
import org.xialing.common.dto.route.invoke.InvokeResult;
import org.xialing.common.dto.route.query.QueryRequest;
import org.xialing.common.dto.route.query.QueryResult;
import org.xialing.common.enums.ResponseCodeEnum;
import org.xialing.fabric.service.FabricService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author leon
 * @version 1.0
 * @date 2019/10/8 23:07
 */
@RestController
@Slf4j
@RequestMapping("fabric")
public class FabricController{

    @Resource
    private FabricService fabricService;

    @PostMapping("route/invoke")
    public R<InvokeResult> invoke(@RequestBody InvokeRequest invokeRequest){
        try {
            return R.success(fabricService.invoke(invokeRequest));
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.error(ResponseCodeEnum.BUSI_ERROR.getCode(),e.getMessage());
        }
    }

    @PostMapping("route/invoke/asyncQ")
    public R<InvokeAsyncQueryResult> invokeAsyncQuery(@RequestBody InvokeAsyncQueryRequest invokeAsyncQueryRequest){
        try {
            return R.success(fabricService.asyncQueryResult(invokeAsyncQueryRequest));
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.error(ResponseCodeEnum.BUSI_ERROR.getCode(),e.getMessage());
        }
    }

    @PostMapping("route/query")
    public R<QueryResult> query(@RequestBody QueryRequest queryRequest){
        try {
            return R.success(fabricService.query(queryRequest));
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.error(ResponseCodeEnum.BUSI_ERROR.getCode(),e.getMessage());
        }
    }

}
