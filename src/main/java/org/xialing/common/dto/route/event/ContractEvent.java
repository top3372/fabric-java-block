package org.xialing.common.dto.route.event;

import lombok.Data;


/**
 * @author leon
 * @version 1.0
 * @date 2020/3/5 10:22
 */
@Data
public class ContractEvent {

        private String chainNetCode;

        private String chainNetKey;

        private String channelCode;

        private String channelKey;

        private String contractCode;

        private String contractKey;

        private String funCode;

        private String funKey;

        private String eventCode;

        private String eventKey;

        private String evenType;

        private String leagueCode;

        private String eventPayLoad;

        private String txId;

        private Long blockNo;





}
