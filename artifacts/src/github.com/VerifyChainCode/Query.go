package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"strconv"
)

func queryListForPage(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Function name queryEstimateListForPage")
	}
	pageSize, err := strconv.ParseInt(args[1], 10, 32)
	if err != nil {
		return shim.Error(err.Error())
	}
	bookmark := args[2]


	queryString := fmt.Sprintf("{\"selector\":{\"_id\":{\"$gt\":null},\"businessNumber\":\"%s\"}," +
		"\"fields\":[\"recordId\",\"businessNumber\",\"businessTypeId\",\"idNo\",\"realName\"," +
		"\"mobile\",\"bankCard\",\"source\",\"hashData\","+
		"\"remark\",\"verifyDate\",\"txId\"]," +
		"\"sort\":[{\"_id\":\"desc\"}]," +
		"\"execution_stats\":true}", args[0])

	resultsIterator, responseMetadata, err := stub.GetQueryResultWithPagination(queryString,int32(pageSize),bookmark)
	if err!=nil{
		return shim.Error("queryListForPage query failed" + err.Error())
	}
	recordList,err:=getPaginationQueryResults(resultsIterator,responseMetadata)
	if err!=nil{
		return shim.Error("queryListForPage query failed" + err.Error())
	}
	return shim.Success(recordList)

}

func queryListByBusinessNumber(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryListByBusinessNumber")
	}
	queryString := fmt.Sprintf("{\"selector\":{\"_id\":{\"$gt\":null},\"businessNumber\":\"%s\"}," +
		"\"fields\":[\"recordId\",\"businessNumber\",\"businessTypeId\",\"idNo\",\"realName\"," +
		"\"mobile\",\"bankCard\",\"source\",\"hashData\","+
		"\"remark\",\"verifyDate\",\"txId\"]," +
		"\"sort\":[{\"_id\":\"desc\"}]," +
		"\"execution_stats\":true}", args[0])


	resultsIterator,err:= stub.GetQueryResult(queryString)
	if err!=nil{
		return shim.Error("queryListByBusinessNumber query failed" + err.Error())
	}
	recordList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryListByBusinessNumber query failed" + err.Error())
	}
	return shim.Success(recordList)
}


func queryAllListForPage(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Function name queryAllListForPage")
	}
	pageSize, err := strconv.ParseInt(args[0], 10, 32)
	if err != nil {
		return shim.Error(err.Error())
	}
	bookmark := args[1]


	queryString := fmt.Sprintf("{\"selector\":{\"_id\":{\"$gt\":null}}," +
		"\"fields\":[\"recordId\",\"businessNumber\",\"businessTypeId\",\"idNo\",\"realName\"," +
		"\"mobile\",\"bankCard\",\"source\",\"hashData\","+
		"\"remark\",\"verifyDate\",\"txId\"]," +
		"\"sort\":[{\"_id\":\"desc\"}]," +
		"\"execution_stats\":true}")

	resultsIterator, responseMetadata, err := stub.GetQueryResultWithPagination(queryString,int32(pageSize),bookmark)
	if err!=nil{
		return shim.Error("queryAllListForPage query failed" + err.Error())
	}
	recordList,err:=getPaginationQueryResults(resultsIterator,responseMetadata)
	if err!=nil{
		return shim.Error("queryAllListForPage query failed" + err.Error())
	}
	return shim.Success(recordList)

}

func queryAllList(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	queryString := fmt.Sprintf("{\"selector\":{\"_id\":{\"$gt\":null}}," +
		"\"fields\":[\"recordId\",\"businessNumber\",\"businessTypeId\",\"idNo\",\"realName\"," +
		"\"mobile\",\"bankCard\",\"source\",\"hashData\","+
		"\"remark\",\"verifyDate\",\"txId\"]," +
		"\"sort\":[{\"_id\":\"desc\"}]," +
		"\"execution_stats\":true}")


	resultsIterator,err:= stub.GetQueryResult(queryString)
	if err!=nil{
		return shim.Error("queryAllList query failed" + err.Error())
	}
	recordList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryAllList query failed" + err.Error())
	}
	return shim.Success(recordList)
}
