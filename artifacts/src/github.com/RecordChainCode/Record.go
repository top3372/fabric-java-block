package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"fmt"
	"encoding/json"
	"strings"
)

func record(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 15 {
		return shim.Error("Incorrect number of arguments. Function name record")
	}
	//now := time.Now()
	//
	//loc, _ := time.LoadLocation("Asia/Shanghai")//设置时区
	//timeFormat := "2006-01-02 15:04:05"
	//createDate := now.In(loc).Format(timeFormat)
	sc := &RecordStruct{}
	sc.RecordId = args[0]
	sc.BusinessNumber = args[1]
	sc.BusinessTypeId = args[2]
	sc.RecordModelId = args[3]
	sc.RecordModelVersion = args[4]
	sc.RecordType = args[5]
	sc.RecordStep = args[6]
	sc.RecordTarget = args[7]
	sc.RecordSource = args[8]
	sc.RequestRecordId = args[9]
	sc.RecordStatus = args[10]
	sc.RecordData = args[11]
	sc.RecordRemark = args[12]
	sc.RecordBy = args[13]
	sc.RecordDate = args[14]
	sc.TxId = stub.GetTxID()//获取交易txId
	fmt.Println(sc)

	//判断当前步骤 是否已经执行过

	queryString := fmt.Sprintf("{\"selector\":{\"_id\":{\"$gt\":null},\"businessNumber\":\"%s\",\"recordStep\":\"%s\"}," +
		"\"fields\":[\"recordId\",\"businessNumber\",\"businessTypeId\",\"recordModelId\",\"recordModelVersion\"," +
		"\"recordType\",\"recordStep\",\"recordTarget\",\"recordSource\",\"requestRecordId\"," +
		"\"recordStatus\",\"recordBy\",\"recordDate\",\"txId\"]," +
		"\"sort\":[{\"_id\":\"desc\"}]," +
		"\"execution_stats\":true}", args[1],args[6])

	resultsIterator,err:= stub.GetQueryResult(queryString)
	if err!=nil{
		return shim.Error("queryRecordListByBusinessNumber query failed" + err.Error())
	}

	recordList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryRecordListByBusinessNumber query failed" + err.Error())
	}
	if(!strings.EqualFold(string(recordList), "[]")){
		return shim.Error("此业务流水号该节点已经操作过")
	}



	//判断 是否 为 选择模型step
	if(strings.EqualFold(sc.RecordStep, "2")) {
		//判断 业务和模型 是否匹配
		if (strings.EqualFold(sc.BusinessTypeId, "个人房贷") || strings.EqualFold(sc.BusinessTypeId, "个人车贷")){
			if (!strings.EqualFold(sc.RecordModelId, "个人信贷")){
				return shim.Error("个人业务选用模型不正确")
			}
		}else if(strings.EqualFold(sc.BusinessTypeId, "企业创业贷") ){
			if (!strings.EqualFold(sc.RecordModelId, "企业借贷")){
				return shim.Error("企业业务选用模型不正确")
			}
		}
	}









	var key1,_= stub.CreateCompositeKey("Record",[]string{sc.RecordId})
	fmt.Println(key1)

	recordJSON, err1 := json.Marshal(sc)
	fmt.Println(string(recordJSON))
	if err1 != nil {
		fmt.Println(err1.Error())
		return shim.Error(err1.Error())
	} else {
		// Write the recordJSON
		if err := stub.PutState(key1, recordJSON); err != nil {
			return shim.Error(err.Error())
		} else {
			if transientMap, err := stub.GetTransient(); err == nil {
				if transientData, ok := transientMap["result"]; ok {
					return shim.Success(transientData)
				}
			}
			return shim.Success(nil)
		}
	}
}
