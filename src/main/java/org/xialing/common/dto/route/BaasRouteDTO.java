package org.xialing.common.dto.route;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class BaasRouteDTO implements Serializable {

    private Long id;


    private String baasRouteCode;


    private String baasRouteName;


    private String baasRouteDesc;


    private String status;


    private Long seqNum;


    private Date createTime;


    private Date updateTime;


    private String secretId;


    private String secretKey;


    private String endPoint;


    private String region;

    private String className;

    private static final long serialVersionUID = 1L;
}