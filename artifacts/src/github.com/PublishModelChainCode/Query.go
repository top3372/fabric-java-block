package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"fmt"
)

func queryPublishList(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	//if len(args) != 2 {
	//	return shim.Error("Incorrect number of arguments. Function name queryRecordList")
	//}
	//limit ,limitError := strconv.Atoi(args[0])
	//
	//if limitError != nil{
	//	fmt.Println("queryRecordList limit字符串转换成整数失败")
	//}
	//skip ,skipError := strconv.Atoi(args[1])
	//
	//if skipError != nil{
	//	fmt.Println("queryRecordList skip字符串转换成整数失败")
	//}
	queryString := fmt.Sprintf("{\"selector\":{\"_id\":{\"$gt\":null}}," +
		"\"fields\":[\"publishId\",\"modelId\",\"modelName\",\"modelVersion\"," +
		"\"modelOwner\",\"modelType\",\"modelStatus\",\"publishRemark\"," +
		"\"publishDate\",\"publishBy\",\"txId\"]," +
		"\"sort\":[{\"_id\":\"desc\"}]," +
		"\"execution_stats\":true}")

	resultsIterator,err:= stub.GetQueryResult(queryString)
	if err!=nil{
		return shim.Error("queryRecordList query failed" + err.Error())
	}
	recordList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryRecordList query failed" + err.Error())
	}
	return shim.Success(recordList)
}

func queryModelDetailByPublishId(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryModelDetailByPublishId")
	}
	var key1,_= stub.CreateCompositeKey("PublishModel",[]string{args[0]})
	fmt.Println(key1)

	if value, err := stub.GetState(key1); err != nil || value == nil {
		return shim.Error("queryModelDetailByPublishId: invalid ID supplied." + key1)
	} else {
		fmt.Println(value)
		return shim.Success(value)
	}
}

func queryPublishListByModelId(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryPublishListByModelId")
	}

	queryString := fmt.Sprintf("{\"selector\":{\"_id\":{\"$gt\":null},\"modelId\":\"%s\"}," +
		"\"fields\":[\"publishId\",\"modelId\",\"modelName\",\"modelVersion\"," +
		"\"modelOwner\",\"modelType\",\"modelStatus\",\"publishRemark\"," +
		"\"publishDate\",\"publishBy\",\"txId\"]," +
		"\"sort\":[{\"_id\":\"desc\"}]," +
		"\"execution_stats\":true}", args[0])

	resultsIterator,err:= stub.GetQueryResult(queryString)
	if err!=nil{
		return shim.Error("queryPublishListByModelId query failed" + err.Error())
	}
	recordList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryPublishListByModelId query failed" + err.Error())
	}
	return shim.Success(recordList)
}

func  queryHistoryListByPublishId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryHistoryListByPublishId")
	}
	var key1,_= stub.CreateCompositeKey("PublishModel",[]string{args[0]})
	fmt.Println(key1)

	it,err:= stub.GetHistoryForKey(key1)
	if err!=nil{
		return shim.Error("queryHistoryListByPublishId query failed" + err.Error())
	}
	var multiCertificateHistoryList,_= getHistoryListResult(it)
	return shim.Success(multiCertificateHistoryList)

}