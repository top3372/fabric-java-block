# fabric-java-block

[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)

集成springboot和fabric sdk 提供rest api的接口

用swagger 生成api文档

基于Hyperledger Fabric v1.4.0版本...

环境中包含 3个zookeeper 4个kafaka 3个orderer 5个机构下面个4个节点 的分布式部署 (其中IP地址需要根据环境修改)  

其中 运行时需要把 证书下面的内容 拷贝到orderer和peer文件夹下面


api说明

| 接口	        | 链接           | 其他 |
| ------------- |:-------------:| -----:|
| 申请用户 | /user/enroll |  |
| 创建通道 | /channel/create |  |
| 加入通道 | /channel/join |  |
| 安装合约 | /chaincode/install |  |
| 初始化合约 | /chaincode/instantiate |  |
| 更新合约 | /chaincode/upgrade |  |
| invoke合约 | /chaincode/invoke |  |
| 查询合约 | /chaincode/query |  |
| 根据Tx_id查询交易的BlockInfo | /blockInfo/withTxid |  |

sdk工程目录下面artifacts\channel 包含了 证书信息等
              artifacts\src\github.com 目录下面 包含了3个智能合约,可以供大家测试

api 接口调用详情可以参考 fabric区块链api接口.docx

联系 email top3372@163.com

