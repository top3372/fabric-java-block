package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"fmt"
	"strings"
	"bytes"
	"encoding/json"
	"time"
)

type SingleCertificateStore struct{}

type SingleCertificate struct {
	UserId    string `json:"userId"`
	CertificateId string `json:"certificateId"`
	CertificateType string `json:"certificateType"`
	CertificatePath string `json:"certificatePath"`
	FileName string `json:"fileName"`
	FileHash string `json:"fileHash"`
	FileType string `json:"fileType"`
	FilePath string `json:"filePath"`
	Code string `json:"code"`
	CodeStatus string `json:"codeStatus"`
	Name string `json:"name"`
	Phone string `json:"phone"`
	Email string `json:"email"`
	Note string `json:"note"`
	Type string `json:"type"`
	CreateDate string `json:"createDate"`
}

func main() {
	if err := shim.Start(new(SingleCertificateStore)); err != nil {
		fmt.Printf("Main: Error starting SingleCertificateStore chaincode: %s", err)
	}
}

func (cc *SingleCertificateStore) Init(stub shim.ChaincodeStubInterface) pb.Response {

	return shim.Success(nil)
}

func (cc *SingleCertificateStore) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	// Which function is been called?
	function, args := stub.GetFunctionAndParameters()

	// Turn arguments to lower case
	function = strings.ToLower(function)

	switch function {
		case "saveorupdatesinglecertificate":
			return cc.saveOrUpdateSingleCertificate(stub, args)
		case "querylistbyuserid":
			return cc.queryListByUserId(stub, args)
		case "querydetailbyuseridandcertificateid":
			return cc.queryDetailByUserIdAndCertificateId(stub, args)
		case "queryhistorylistbyuseridandcertificateid":
			return cc.queryHistoryListByUserIdAndCertificateId(stub, args)
		case "querylistbycodeandnameandphone":
			return cc.queryListByCodeAndNameAndPhone(stub, args)
		case "batchauthsinglecertificate":
			return cc.batchAuthSingleCertificate(stub, args)
		default:
			return shim.Error("Invalid method! " + function )
	}
}
func (store *SingleCertificateStore) saveOrUpdateSingleCertificate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
//14
	if len(args) != 14 {
		return shim.Error("Incorrect number of arguments. Function name saveOrUpdateSingleCertificate")
	}
	now := time.Now()

	loc, _ := time.LoadLocation("Asia/Shanghai")//设置时区
	timeFormat := "2006-01-02 15:04:05"
	createDate := now.In(loc).Format(timeFormat)
	sc := &SingleCertificate{args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13],"0",createDate }
	fmt.Println(sc)
	var key1,_= stub.CreateCompositeKey("SingleCertificate",[]string{sc.UserId,sc.CertificateId})
	fmt.Println(key1)

	// marshal SingleCertificate struct to json
	// can be returned directly
	// allows more sophisticated search using couchdb /mq
	singleCertificateJSON, errj := json.Marshal(sc)
	fmt.Println(string(singleCertificateJSON))
	if errj != nil {
		fmt.Println(errj.Error())
		return shim.Error(errj.Error())
	} else {
		// Write the SingleCertificate
		if err := stub.PutState(key1, singleCertificateJSON); err != nil {
			return shim.Error(err.Error())
		} else {
			return shim.Success(nil)
		}
	}


}

func (store *SingleCertificateStore) queryListByUserId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryListByUserId")
	}
	queryString := fmt.Sprintf("{\"selector\":{\"_id\": {\"$gt\": null},\"userId\":\"%s\",\"type\":\"0\"},\"sort\": [{\"_id\": \"desc\"}]}", args[0])
	resultsIterator,err:= stub.GetQueryResult(queryString)//必须是CouchDB才行
	if err!=nil{
		return shim.Error(" queryListByUserId Query by Range failed " + err.Error())
	}
	singleCertificateList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error(" queryListByUserId getListResult failed " + err.Error())
	}
	return shim.Success(singleCertificateList)
}

func (store *SingleCertificateStore) queryDetailByUserIdAndCertificateId(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Function name queryDetailByUserIdAndCertificateId")
	}
	var key1,_= stub.CreateCompositeKey("SingleCertificate",[]string{args[0],args[1]})
	fmt.Println(key1)

	if value, err := stub.GetState(key1); err != nil || value == nil {
		return shim.Error("queryDetailByUserIdAndCertificateId: invalid ID supplied." + key1)
	} else {
		fmt.Println(value)
		return shim.Success(value)
	}


}

func (store *SingleCertificateStore) queryListByCodeAndNameAndPhone(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Function name queryListByCodeAndNameAndPhone")
	}

	queryString := fmt.Sprintf("{\"selector\":{\"_id\": {\"$gt\": null},\"code\":\"%s\",\"name\":\"%s\",\"phone\":\"%s\",\"type\":\"0\"},\"sort\": [{\"_id\": \"desc\"}]}", args[0],args[1],args[2])
	resultsIterator,err:= stub.GetQueryResult(queryString)//必须是CouchDB才行
	if err!=nil{
		return shim.Error("queryListByCodeAndNameAndPhone query failed" + err.Error())
	}
	singleCertificateList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryListByCodeAndNameAndPhone query failed" + err.Error())
	}
	return shim.Success(singleCertificateList)
}

func (store *SingleCertificateStore) queryHistoryListByUserIdAndCertificateId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Function name queryHistoryListByUserIdAndCertificateId")
	}
	var key1,_= stub.CreateCompositeKey("SingleCertificate",[]string{args[0],args[1]})
	fmt.Println(key1)

	it,err:= stub.GetHistoryForKey(key1)
	if err!=nil{
		return shim.Error("queryHistoryListByUserIdAndCertificateId query failed" + err.Error())
	}
	var singleCertificateHistoryList,_= getHistoryListResult(it)
	return shim.Success(singleCertificateHistoryList)

}

func getListResult(resultsIterator shim.StateQueryIteratorInterface) ([]byte,error){

	defer resultsIterator.Close()
	// buffer is a JSON array containing QueryRecords
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		//buffer.WriteString("{")
		//buffer.WriteString("\"Key\":")
		//buffer.WriteString("\"")
		//buffer.WriteString(queryResponse.Key)
		//buffer.WriteString("\"")
		//
		//buffer.WriteString(", " )
		//buffer.WriteString("\"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		//buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")
	fmt.Printf("queryResult:\n%s\n", buffer.String())
	return buffer.Bytes(), nil
}

func getHistoryListResult(resultsIterator shim.HistoryQueryIteratorInterface) ([]byte,error){

	defer resultsIterator.Close()
	// buffer is a JSON array containing QueryRecords
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		item,_:= json.Marshal( queryResponse)
		buffer.Write(item)
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")
	fmt.Printf("queryResult:\n%s\n", buffer.String())
	return buffer.Bytes(), nil
}


func (store *SingleCertificateStore) batchAuthSingleCertificate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 6 {
		return shim.Error("Incorrect number of arguments. Function name batchAuthSingleCertificate")
	}

	userId := args[0]
	for _, v := range strings.Split(args[1], ",") {
		fmt.Println(v)
		var key1,_= stub.CreateCompositeKey("SingleCertificate",[]string{userId,v})
		fmt.Println(key1)

		if value, err := stub.GetState(key1); err != nil || value == nil {
			fmt.Println( "error:" , err )
			continue
		} else {
			fmt.Println(value)
			var singleCertificate SingleCertificate
			 error := json.Unmarshal(value,&singleCertificate)
			if error != nil {
				fmt.Println( "error:" , error )
				continue
			}
			singleCertificate.Code = args[2]
			singleCertificate.Name = args[3]
			singleCertificate.Phone = args[4]
			singleCertificate.CodeStatus = args[5]
			singleCertificateJSON, _ := json.Marshal(singleCertificate)
			fmt.Println(string(singleCertificateJSON))
			if errSave := stub.PutState(key1, singleCertificateJSON); errSave != nil {
				fmt.Println( "error:" , errSave )
				continue
			}
		}
	}
	return shim.Success(nil)
}