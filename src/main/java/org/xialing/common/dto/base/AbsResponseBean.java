package org.xialing.common.dto.base;

import java.io.Serializable;
import java.util.Date;

public abstract class AbsResponseBean<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6632899838939719803L;

    private String reqId = "";
    private String version = "1";
    private String status;
    private String errorCode;
    private String errorMessage;
    private Date dateTime = new Date();
    private String reserve;

    public AbsResponseBean() {
        super();
    }

    public AbsResponseBean(String reqId, String version, String status, String errorCode, String errorMessage,
                           Date dateTime, String reserve) {
        super();
        this.reqId = reqId;
        this.version = version;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.dateTime = dateTime;
        this.reserve = reserve;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }
}
