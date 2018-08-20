package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"fmt"
	"encoding/json"
)

func  publish(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 11 {
		return shim.Error("Incorrect number of arguments. Function name publish")
	}
	//now := time.Now()
	//
	//loc, _ := time.LoadLocation("Asia/Shanghai")//设置时区
	//timeFormat := "2006-01-02 15:04:05"
	//createDate := now.In(loc).Format(timeFormat)
	sc := &PublishModelStruct{}
	sc.PublishId = args[0]
	sc.ModelId= args[1]
	sc.ModelName = args[2]
	sc.ModelVersion = args[3]
	sc.ModelOwner = args[4]
	sc.ModelType = args[5]
	sc.ModelStatus = args[6]
	sc.ModelData = args[7]
	sc.PublishRemark = args[8]
	sc.PublishBy = args[9]
	sc.PublishDate = args[10]
	sc.TxId = stub.GetTxID()

	fmt.Println(sc)
	var key1,_= stub.CreateCompositeKey("PublishModel",[]string{sc.PublishId})
	fmt.Println(key1)

	publishModelJSON, errj := json.Marshal(sc)
	fmt.Println(string(publishModelJSON))
	if errj != nil {
		fmt.Println(errj.Error())
		return shim.Error(errj.Error())
	} else {
		// Write the publishModelJSON
		if err := stub.PutState(key1, publishModelJSON); err != nil {
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
