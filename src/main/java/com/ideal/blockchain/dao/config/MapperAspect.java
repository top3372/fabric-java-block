package com.ideal.blockchain.dao.config;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Created by user on 2017/10/25.
 */
@Aspect
@Component
public class MapperAspect {

//    /*
//     * 通过连接点切入
//     */
//    @Before("execution(* blockchain.dao.mapper.*.insert*(..)) &&" + "args(model,..)")
//    public void beforeInsert(BaseModel model) {
//        if (model.getCreateDate() == null) {
//            model.setCreateDate(new Date());
//        }
//        model.setUpdateDate(new Date());
//
//    }
//
//    /*
//    * 通过连接点切入
//    */
//    @Before("execution(* blockchain.dao.mapper.*.update*(..)) &&" + "args(model,..)")
//    public void before(BaseModel model) {
//        model.setUpdateDate(new Date());
//
//    }


}
