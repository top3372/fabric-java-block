package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"fmt"
)

type SimpleAsset struct {
}

// Init is called during chaincode instantiation to initialize any data.
func (t *SimpleAsset) Init(stub shim.ChaincodeStubInterface) peer.Response {

	return shim.Success(nil)
}

func (t *SimpleAsset) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	fmt.Println("########### haikou Invoke ###########")
	function, args := stub.GetFunctionAndParameters()
	if function != "invoke" {
		return shim.Error("Unknown function call")
	}

	if len(args) < 2 {
		return shim.Error("Incorrect number of arguments. Expecting at least 2")
	}

	if args[0] == "query" {
		// queries an entity state
		return t.query(stub, args)
	}
	if args[0] == "submit" {
		// Deletes an entity from its state
		return t.submit(stub, args)
	}
	return shim.Error("Unknown action, check the first argument, must be one of 'query', or 'submit'")

}


func (asset *SimpleAsset) query(stubInterface shim.ChaincodeStubInterface, args []string) peer.Response {
	var A string // Entities
	var err error

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
	}
	A = args[1]
	Avalbytes, err := stubInterface.GetState(A)

	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + A + "\"}"
		return shim.Error(jsonResp)
	}

	if Avalbytes == nil {
		jsonResp := "{\"Error\":\"Nil for " + A + "\"}"
		return shim.Error(jsonResp)
	}

	jsonResp := "{\"contractName\":\"" + A + "\",\"contractHash\":\"" + string(Avalbytes) + "\"}"
	fmt.Printf("Query Response:%s\n", jsonResp)
	return shim.Success(Avalbytes)

}


func (asset *SimpleAsset) submit(stubInterface shim.ChaincodeStubInterface, args []string) peer.Response {
	fmt.Println("submit")


	var A string    // Entities
	var Aval string // Asset holdings
	var err error

	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	fmt.Println(args[1]+args[2])
	A = args[1]
	Aval = args[2]

	err = stubInterface.PutState(A, []byte(Aval))
	if err != nil {
		return shim.Error(err.Error())
	}

	if transientMap, err := stubInterface.GetTransient(); err == nil {
		if transientData, ok := transientMap["result"]; ok {
			return shim.Success(transientData)
		}
	}
	return shim.Success(nil)

}

func main() {
	err := shim.Start(new(SimpleAsset))
	if err != nil {
		fmt.Printf("Error starting Simple SimpleAsset: %s", err)
	}
}