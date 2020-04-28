package org.xialing.common.dto.route.blockinfo;

import lombok.Data;

/**
 * @author leon
 * @version 1.0
 * @date 2019/11/11 11:35
 */
@Data
public class BlockSummary {
    private Long	totalChannelCount;		//网络通道总数量
    private Long	myChannelCount;	    	//当前组织创建的通道数量
    private Long	joinChannelCount;		//当前组织加入的通道数量
    private Long	totalPeerCount;	    	//网络节点总数量
    private Long	myPeerCount;      	    //当前组织创建的节点数量
    private Long	orderCount;	        	//其他组织创建的节点数量
    private Long	totalGroupCount;	    //	网络组织总数量
    private Long	myGroupCount;	    	//当前组织创建的组织数量
    private Long	totalChaincodeCount;	//	网络智能合约总数量
    private Long	recentChaincodeCount;	//最近7天发起的智能合约数量
    private Long	myChaincodeCount;		//当前组织发起的智能合约数量
    private Long	totalCertCount;	    	//当前组织的证书总数量
    private Long	tlsCertCount;	    	//颁发给当前组织的证书数量
    private Long	peerCertCount;	    	//网络背书节点证书数量
    private Long	clientCertCount;	    //	当前组织业务证书数量
    private String	requestId;	        	//唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。

}
