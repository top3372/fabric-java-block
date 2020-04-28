package org.xialing.common.exception;

import org.xialing.common.enums.ResponseCodeEnum;

/**
 * @author leon
 * @version 1.0
 * @date 2020/4/28 11:40
 */
public class ServiceException extends RuntimeException {
    private String code;

    private String desc;

    public ServiceException(String desc) {
        this.code = code;
        this.desc = desc;
    }

    public ServiceException(String code, String desc) {

        this.code = code;
        this.desc = desc;
    }

    public ServiceException(ResponseCodeEnum respCodeEnum, String desc) {

        this.code = respCodeEnum.getCode();
        this.desc = desc;
    }

    public ServiceException(ResponseCodeEnum respCodeEnum) {

        this.code = respCodeEnum.getCode();
        this.desc = respCodeEnum.getDesc();
    }

    public ServiceException(ResponseCodeEnum respCodeEnum, Throwable cause) {

        this.code = respCodeEnum.getCode();
        this.desc = respCodeEnum.getDesc();
    }

    public ServiceException(ResponseCodeEnum respCodeEnum, String msg, Throwable cause) {


        this.code = respCodeEnum.getCode();
        this.desc = respCodeEnum.getDesc();
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
