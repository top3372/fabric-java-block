package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"strings"
)

type EstimateChainCodeStore struct{}

type EstimateStruct struct {
	RecordId  string `json:"recordId"`
	BusinessNumber string `json:"businessNumber"`
	BusinessTypeId	string `json:"businessTypeId"`
	EstimateModelId string `json:"estimateModelId"`
	EstimateModelVersion string `json:"estimateModelVersion"`
	EstimateModelType string `json:"estimateModelType"`
	HashData string  `json:"hashData"`
	Remark string `json:"remark"`
	EstimateDate string `json:"estimateDate"`
	TxId string `json:"txId"`
}

func main() {
	if err := shim.Start(new(EstimateChainCodeStore)); err != nil {
		fmt.Printf("Main: Error starting RecordChainCodeStore chaincode: %s", err)
	}
}

func (cc *EstimateChainCodeStore) Init(stub shim.ChaincodeStubInterface) pb.Response {

	return shim.Success(nil)
}

func (cc *EstimateChainCodeStore) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	// Which function is been called?
	function, args := stub.GetFunctionAndParameters()

	// Turn arguments to lower case
	function = strings.ToLower(function)

	switch function {
		case "estimate":
			return estimate(stub, args)
		case "querypagebybn":
			return queryEstimateListForPageByBusinessNumber(stub,args)
		case "querybybn":
			return queryListByBusinessNumber(stub,args)
		case "queryestimatelistforpage" :
			return queryEstimateListForPage(stub,args)
		case "querylist":
			return queryList(stub,args)
		default:
			return shim.Error("Invalid method! " + function )
	}
}