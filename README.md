# fabric-java-block
集成springboot和fabric sdk 提供rest api的接口

用swagger 生成api文档

基于Hyperledger Fabric v1.3.0版本...

环境中包含 3个zookeeper 4个kafaka 3个orderer 5个机构下面个4个节点 的分布式部署  

其中 运行时需要把 证书下面的内容 拷贝到orderer和peer文件夹下面


api说明

| 接口	        | 链接           | 其他 |
| ------------- |:-------------:| -----:|
| 申请用户 | /enroll |  |
| 创建通道 | /api/construct |  |
| 安装合约 | /api/install |  |
| 初始化合约 | /api/instantiate |  |
| 更新合约 | /api/upgrade |  |
| 加入通道 | /api/join |  |
| invoke合约 | /api/invoke |  |
| 查询合约 | /api/query |  |
| 根据Tx_id查询交易所在区块高度信息 | /api/block/txid |  |

sdk工程目录下面artifacts\channel 包含了 证书信息等
              artifacts\src\github.com 目录下面 包含了3个智能合约,可以供大家测试

api 接口调用详情可以参考 fabric区块链api接口.docx

联系 QQ 181645929

