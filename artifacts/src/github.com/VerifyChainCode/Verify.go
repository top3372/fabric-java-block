package main

import (
	"crypto/md5"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type CallBackPar struct {
	ORDERNUM string `json:"ordernum"`
	RESULT  string `json:"result"`
	SOURCE  string `json:"source"`
	MODEL   string `json:"model"`
}

func verify(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 12 {
		return shim.Error("Incorrect number of arguments. Function name verify")
	}


	sc := &VerifyStruct{}
	sc.RecordId = args[0]
	sc.BusinessNumber = args[1]
	sc.BusinessTypeId = args[2]
	//身份证,姓名,手机号,银行卡号 请求参数
	sc.IdNo = args[3]
	sc.RealName = args[4]
	sc.Mobile = args[5]
	sc.BankCard = args[6]

	sc.Remark = ""
	sc.Source = args[7]
	sc.VerifyDate = args[8]
	sc.TxId = stub.GetTxID()//获取交易txId
	sc.ModelName = args[9]



	//判断当前步骤 是否已经执行过


	//调用url
	url := args[10]
	parameterString := args[11]
	fmt.Println(url)
	fmt.Println(parameterString)
	//var jsonStr = []byte(parameterString)

	resp, errhttp := urlFunForm(url,parameterString)
	if errhttp != nil {
		fmt.Println(errhttp)
		return shim.Error(errhttp.Error())
	}

	responseString := string(resp)
	fmt.Println(responseString)

	//MD5 取Hash值
	signByte := []byte(responseString)
	hash := md5.New()
	hash.Write(signByte)
	hex := hex.EncodeToString(hash.Sum(nil))
	fmt.Printf(hex)

	sc.HashData = hex


	var key1,_= stub.CreateCompositeKey("Verify",[]string{sc.RecordId})
	fmt.Println(key1)

	recordJSON, err2 := json.Marshal(sc)
	fmt.Println(string(recordJSON))
	if err2 != nil {
		fmt.Println(err2.Error())
		return shim.Error(err2.Error())
	} else {
		// Write the recordJSON
		if err := stub.PutState(key1, recordJSON); err != nil {
			return shim.Error(err.Error())
		} else {
			var callBackPar CallBackPar
			callBackPar.ORDERNUM = sc.BusinessNumber
			callBackPar.RESULT = responseString
			callBackPar.SOURCE = sc.Source
			callBackPar.MODEL = sc.ModelName

			//调回调接口
			if bs, errbs := json.Marshal(callBackPar); errbs == nil {
				fmt.Println(string(bs))
				urlFunJson(UrlCallback,bs)
			} else {
				fmt.Println(errbs)
			}
			if transientMap, err3 := stub.GetTransient(); err3 == nil {
				if transientData, ok := transientMap["result"]; ok {

					return shim.Success(transientData)
				}
			}else{
				fmt.Println(err3)
			}
			return shim.Success(nil)
		}
	}
}
