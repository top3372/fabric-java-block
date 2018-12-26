package main

import  (
	"crypto/md5"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

func estimate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 9 {
		return shim.Error("Incorrect number of arguments. Function name estimate")
	}

	sc := &EstimateStruct{}
	sc.RecordId = args[0]
	sc.BusinessNumber = args[1]
	sc.BusinessTypeId = args[2]
	sc.EstimateModelId = args[3]
	sc.EstimateModelVersion = args[4]
	sc.EstimateModelType = args[5]
	sc.Remark = args[7]
	sc.EstimateDate = args[8]
	sc.TxId = stub.GetTxID()//获取交易txId

	//MD5 取Hash值
	signByte := []byte(args[6])
	hash := md5.New()
	hash.Write(signByte)
	hex := hex.EncodeToString(hash.Sum(nil))
	fmt.Printf(hex)

	sc.HashData = hex

	fmt.Println(sc)

	var key1,_= stub.CreateCompositeKey("Estimate",[]string{sc.RecordId})
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
			if transientMap, err2 := stub.GetTransient(); err2 == nil {
				if transientData, ok := transientMap["result"]; ok {
					return shim.Success(transientData)
				}
			}else{
				fmt.Println(err2)
			}
			return shim.Success(nil)
		}
	}

}
