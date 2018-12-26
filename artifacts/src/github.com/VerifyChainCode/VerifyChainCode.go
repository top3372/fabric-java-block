package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"strings"
)

type VerifyChainCodeStore struct{}

type VerifyStruct struct {
	RecordId  string `json:"recordId"`
	BusinessNumber string `json:"businessNumber"`
	BusinessTypeId	string `json:"businessTypeId"`
	IdNo string `json:"idNo"`
	RealName string `json:"realName"`
	Mobile string `json:"mobile"`
	BankCard string `json:"bankCard"`
	Source string `json:"source"`
	HashData string  `json:"hashData"`
	Remark string `json:"remark"`
	VerifyDate string `json:"verifyDate"`
	ModelName string `json:"modelName"`
	TxId string `json:"txId"`
}

func main() {
	if err := shim.Start(new(VerifyChainCodeStore)); err != nil {
		fmt.Printf("Main: Error starting RecordChainCodeStore chaincode: %s", err)
	}
}

func (cc *VerifyChainCodeStore) Init(stub shim.ChaincodeStubInterface) pb.Response {

	return shim.Success(nil)
}

func (cc *VerifyChainCodeStore) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	// Which function is been called?
	function, args := stub.GetFunctionAndParameters()

	// Turn arguments to lower case
	function = strings.ToLower(function)

	switch function {
		case "verify":
			return verify(stub, args)
		case "querylistforpage":
			return queryListForPage(stub, args)
		case "querylistbybusinessnumber":
			return queryListByBusinessNumber(stub, args)
		case "queryalllistforpage":
			return queryAllListForPage(stub, args)
		case "queryalllist":
			return queryAllList(stub, args)
		default:
			return shim.Error("Invalid method! " + function )
	}
}