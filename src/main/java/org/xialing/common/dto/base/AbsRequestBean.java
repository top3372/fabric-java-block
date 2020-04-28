package org.xialing.common.dto.base;

import java.io.Serializable;
import java.util.Date;

public abstract class AbsRequestBean<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2658930134054157312L;
    private String reqId = "";
    private String version = "1";
    private String saleMerchId;
    private Date dateTime = new Date();
    private String reserve;

    public AbsRequestBean() {
        super();
    }

    public AbsRequestBean(String reqId, String version, String saleMerchId, Date dateTime, String reserve) {
        super();
        this.reqId = reqId;
        this.version = version;
        this.saleMerchId = saleMerchId;
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

    public String getSaleMerchId() {
        return saleMerchId;
    }

    public void setSaleMerchId(String saleMerchId) {
        this.saleMerchId = saleMerchId;
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
