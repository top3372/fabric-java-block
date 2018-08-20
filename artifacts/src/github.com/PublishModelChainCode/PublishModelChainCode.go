package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"fmt"
	"strings"
)

type PublishModelChainCodeStore struct{}

type PublishModelStruct struct {
	PublishId string `json:"publishId"`
	ModelId   string `json:"modelId"`
	ModelName string `json:"modelName"`
	ModelVersion string `json:"modelVersion"`
	ModelOwner string `json:"modelOwner"`
	ModelType  string `json:"modelType"`
	ModelStatus string `json:"modelStatus"`
	ModelData string  `json:"modelData"`
	PublishRemark	   string `json:"publishRemark"`
	PublishDate string `json:"publishDate"`
	PublishBy string `json:"publishBy"`
	TxId string `json:"txId"`
}

func main() {
	if err := shim.Start(new(PublishModelChainCodeStore)); err != nil {
		fmt.Printf("Main: Error starting PublishModelChainCodeStore chaincode: %s", err)
	}
}

func (cc *PublishModelChainCodeStore) Init(stub shim.ChaincodeStubInterface) pb.Response {

	return shim.Success(nil)
}

func (cc *PublishModelChainCodeStore) Invoke(stub shim.ChaincodeStubInterface) pb.Response {

	// Which function is been called?
	function, args := stub.GetFunctionAndParameters()

	// Turn arguments to lower case
	function = strings.ToLower(function)

	switch function {
		case "publish":
			return publish(stub, args)
		case "querypublishlist":
			return queryPublishList(stub, args)
		case "querymodeldetailbypublishid":
			return queryModelDetailByPublishId(stub, args)
		case "querypublishlistbymodelid":
			return queryPublishListByModelId(stub, args)
	case "queryhistorylistbypublishid":
		return queryHistoryListByPublishId(stub, args)
		default:
			return shim.Error("Invalid method! " + function )
	}
}
