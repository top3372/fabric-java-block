package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"fmt"
)

func queryRecordList(stub shim.ChaincodeStubInterface, args []string) pb.Response {


	queryString := fmt.Sprintf("{\"selector\":{\"_id\":{\"$gt\":null}}," +
		"\"fields\":[\"recordId\",\"businessNumber\",\"businessTypeId\",\"recordModelId\",\"recordModelVersion\"," +
		"\"recordType\",\"recordStep\",\"recordTarget\",\"recordSource\",\"requestRecordId\"," +
		"\"recordStatus\",\"recordBy\",\"recordDate\",\"txId\"]," +
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

func queryRecordDetailByRecordId(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryRecordDetailByRecordId")
	}
	var key1,_= stub.CreateCompositeKey("Record",[]string{args[0]})
	fmt.Println(key1)

	if value, err := stub.GetState(key1); err != nil || value == nil {
		return shim.Error("queryRecordDetailByRecordId: invalid ID supplied." + key1)
	} else {
		fmt.Println(value)
		return shim.Success(value)
	}
}

func queryRecordListByBusinessNumber(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryRecordListByBusinessNumber")
	}

	queryString := fmt.Sprintf("{\"selector\":{\"_id\":{\"$gt\":null},\"businessNumber\":\"%s\"}," +
		"\"fields\":[\"recordId\",\"businessNumber\",\"businessTypeId\",\"recordModelId\",\"recordModelVersion\"," +
		"\"recordType\",\"recordStep\",\"recordTarget\",\"recordSource\",\"requestRecordId\"," +
		"\"recordStatus\",\"recordBy\",\"recordDate\",\"txId\"]," +
		"\"sort\":[{\"_id\":\"desc\"}]," +
		"\"execution_stats\":true}", args[0])

	resultsIterator,err:= stub.GetQueryResult(queryString)
	if err!=nil{
		return shim.Error("queryRecordListByBusinessNumber query failed" + err.Error())
	}
	recordList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryRecordListByBusinessNumber query failed" + err.Error())
	}
	return shim.Success(recordList)
}


func  queryHistoryListByRecordId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryHistoryListByRecordId")
	}
	var key1,_= stub.CreateCompositeKey("Record",[]string{args[0]})
	fmt.Println(key1)

	it,err:= stub.GetHistoryForKey(key1)
	if err!=nil{
		return shim.Error("queryHistoryListByRecordId query failed" + err.Error())
	}
	var multiCertificateHistoryList,_= getHistoryListResult(it)
	return shim.Success(multiCertificateHistoryList)

}

func queryRecordListByTarget(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	return shim.Success(nil)
}

func queryRecordListBySource(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	return shim.Success(nil)
}




