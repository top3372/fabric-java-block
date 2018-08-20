package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"fmt"
	"strings"
)

type RecordChainCodeStore struct{}

type RecordStruct struct {
	RecordId  string `json:"recordId"`
	BusinessNumber string `json:"businessNumber"`
	BusinessTypeId	string `json:"businessTypeId"`
	RecordModelId string `json:"recordModelId"`
	RecordModelVersion string `json:"recordModelVersion"`
	RecordType  string `json:"recordType"`
	RecordStep  string `json:"recordStep"`
	RecordTarget string `json:"recordTarget"`
	RecordSource string `json:"recordSource"`
	RequestRecordId  string `json:"requestRecordId"`
	RecordStatus string `json:"recordStatus"`
	RecordData string  `json:"recordData"`
	RecordRemark	   string `json:"recordRemark"`
	RecordBy string `json:"recordBy"`
	RecordDate string `json:"recordDate"`
	TxId string `json:"txId"`
}

func main() {
	if err := shim.Start(new(RecordChainCodeStore)); err != nil {
		fmt.Printf("Main: Error starting RecordChainCodeStore chaincode: %s", err)
	}
}

func (cc *RecordChainCodeStore) Init(stub shim.ChaincodeStubInterface) pb.Response {

	return shim.Success(nil)
}

func (cc *RecordChainCodeStore) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	// Which function is been called?
	function, args := stub.GetFunctionAndParameters()

	// Turn arguments to lower case
	function = strings.ToLower(function)

	switch function {
		case "record":
			return record(stub, args)
	    case "queryrecordlist":
			return queryRecordList(stub, args)
		case "queryrecorddetailbyrecordid":
			return queryRecordDetailByRecordId(stub, args)
		case "queryrecordlistbybusinessnumber":
			return queryRecordListByBusinessNumber(stub, args)
	case "queryhistorylistbyrecordid":
		return queryHistoryListByRecordId(stub, args)
		default:
			return shim.Error("Invalid method! " + function )
	}
}
