package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"fmt"
	"strings"
	"time"
	"encoding/json"
	"bytes"
)

type MultiCertificateStore struct{}

type MultiCertificate struct {
	UnionId   string `json:"unionId"`
	UserId    string `json:"userId"`
	CertificateType string `json:"certificateType"`
	CertificateId string `json:"certificateId"`
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
	Status string `json:"status"`
	Initiator string `json:"initiator"`
	Type string `json:"type"`
	CreateDate string `json:"createDate"`
}

func main() {
	if err := shim.Start(new(MultiCertificateStore)); err != nil {
		fmt.Printf("Main: Error starting MultiCertificateStore chaincode: %s", err)
	}
}

func (cc *MultiCertificateStore) Init(stub shim.ChaincodeStubInterface) pb.Response {

	return shim.Success(nil)
}

func (cc *MultiCertificateStore) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	// Which function is been called?
	function, args := stub.GetFunctionAndParameters()

	// Turn arguments to lower case
	function = strings.ToLower(function)

	switch function {
		case "saveorupdatemulticertificate":
			return cc.saveOrUpdateMultiCertificate(stub, args)
		case "querylistbyuserid":
			return cc.queryListByUserId(stub, args)
		case "querydetailbyunionidanduserid":
			return cc.queryDetailByUnionIdAndUserId(stub, args)
		case "querylistbyunoinid":
			return cc.queryListByUnoinId(stub, args)
		case "querylistbycodeandnameandphone":
			return cc.queryListByCodeAndNameAndPhone(stub, args)
		case "batchauthmulticertificate":
			return cc.batchAuthMultiCertificate(stub, args)
		case "queryhistorylistbyunionidanduserid":
			return cc.queryHistoryListByUnionIdAndUserId(stub, args)
		default:
			return shim.Error("Invalid method! " + function )
	}
}
func (store *MultiCertificateStore) saveOrUpdateMultiCertificate(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 11 {
		return shim.Error("Incorrect number of arguments. Function name saveOrUpdateMultiCertificate")
	}
	now := time.Now()

	loc, _ := time.LoadLocation("Asia/Shanghai")//设置时区
	timeFormat := "2006-01-02 15:04:05"
	createDate := now.In(loc).Format(timeFormat)
	sc := &MultiCertificate{}
	sc.UnionId = args[0]
	sc.UserId = args[1]
	sc.CertificateType = args[2]
	sc.CertificateId = args[3]
	sc.CertificatePath = args[4]
	sc.FileName = args[5]
	sc.FileHash = args[6]
	sc.FilePath = args[7]
	sc.CodeStatus = args[8]
	sc.Status = args[9]
	sc.Initiator = args[10]
	sc.Type = "1" ///多人存证
	sc.CreateDate = createDate
	fmt.Println(sc)
	var key1,_= stub.CreateCompositeKey("MultiCertificate",[]string{sc.UnionId,sc.UserId})
	fmt.Println(key1)

	multiCertificateJSON, errj := json.Marshal(sc)
	fmt.Println(string(multiCertificateJSON))
	if errj != nil {
		fmt.Println(errj.Error())
		return shim.Error(errj.Error())
	} else {
		// Write the MultiCertificate
		if err := stub.PutState(key1, multiCertificateJSON); err != nil {
			return shim.Error(err.Error())
		} else {
			return shim.Success(nil)
		}
	}
}



func (store *MultiCertificateStore) batchAuthMultiCertificate(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 8 {
		return shim.Error("Incorrect number of arguments. Function name batchAuthMultiCertificate")
	}

	userId := args[0]
	for _, v := range strings.Split(args[1], ",") {
		fmt.Println(v)
		var key1,_= stub.CreateCompositeKey("MultiCertificate",[]string{v,userId})
		fmt.Println(key1)

		if value, err := stub.GetState(key1); err != nil || value == nil {
			fmt.Println( "error:" , err )
			continue
		} else {
			fmt.Println(value)
			var multiCertificate MultiCertificate
			error := json.Unmarshal(value,&multiCertificate)
			if error != nil {
				fmt.Println( "error:" , error )
				continue
			}
			multiCertificate.Code = args[2]
			multiCertificate.CodeStatus = args[3]
			multiCertificate.Name = args[4]
			multiCertificate.Phone = args[5]
			multiCertificate.Email = args[6]
			multiCertificate.Note = args[7]

			multiCertificateJSON, _ := json.Marshal(multiCertificate)
			fmt.Println(string(multiCertificateJSON))
			if errSave := stub.PutState(key1, multiCertificateJSON); errSave != nil {
				fmt.Println( "error:" , errSave )
				continue
			}
		}
	}
	return shim.Success(nil)
}


func (store *MultiCertificateStore) queryListByUserId(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryListByUserId")
	}

	queryString := fmt.Sprintf("{\"selector\":{\"_id\": {\"$gt\": null},\"userId\":\"%s\",\"type\":\"1\"},\"sort\": [{\"_id\": \"desc\"}]}", args[0])
	resultsIterator,err:= stub.GetQueryResult(queryString)//必须是CouchDB才行
	if err!=nil{
		return shim.Error("queryListByUserId query failed" + err.Error())
	}
	multiCertificateList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryListByUserId query failed" + err.Error())
	}
	return shim.Success(multiCertificateList)
}


func (store *MultiCertificateStore) queryDetailByUnionIdAndUserId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Function name queryDetailByUnionIdAndUserId")
	}
	var key1,_= stub.CreateCompositeKey("MultiCertificate",[]string{args[0],args[1]})
	fmt.Println(key1)

	if value, err := stub.GetState(key1); err != nil || value == nil {
		return shim.Error("queryDetailByUnionIdAndUserId: invalid ID supplied." + key1)
	} else {
		fmt.Println(value)
		return shim.Success(value)
	}
}


func (store *MultiCertificateStore) queryListByUnoinId(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Function name queryListByUnoinId")
	}

	queryString := fmt.Sprintf("{\"selector\":{\"_id\": {\"$gt\": null},\"unionId\":\"%s\",\"type\":\"1\"},\"sort\": [{\"_id\": \"desc\"}]}", args[0])
	resultsIterator,err:= stub.GetQueryResult(queryString)//必须是CouchDB才行
	if err!=nil{
		return shim.Error("queryListByUnoinId query failed" + err.Error())
	}
	multiCertificateList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryListByUnoinId query failed" + err.Error())
	}
	return shim.Success(multiCertificateList)

}


func (store *MultiCertificateStore) queryListByCodeAndNameAndPhone(stub shim.ChaincodeStubInterface, args []string) pb.Response {


	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Function name queryListByCodeAndNameAndPhone")
	}

	queryString := fmt.Sprintf("{\"selector\":{\"_id\": {\"$gt\": null},\"code\":\"%s\",\"name\":\"%s\",\"phone\":\"%s\",\"type\":\"1\"},\"sort\": [{\"_id\": \"desc\"}]}", args[0],args[1],args[2])
	resultsIterator,err:= stub.GetQueryResult(queryString)//必须是CouchDB才行
	if err!=nil{
		return shim.Error("queryListByCodeAndNameAndPhone query failed" + err.Error())
	}
	multiCertificateList,err:=getListResult(resultsIterator)
	if err!=nil{
		return shim.Error("queryListByCodeAndNameAndPhone query failed" + err.Error())
	}
	return shim.Success(multiCertificateList)
}


func (store *MultiCertificateStore) queryHistoryListByUnionIdAndUserId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Function name queryHistoryListByUnionIdAndUserId")
	}
	var key1,_= stub.CreateCompositeKey("MultiCertificate",[]string{args[0],args[1]})
	fmt.Println(key1)

	it,err:= stub.GetHistoryForKey(key1)
	if err!=nil{
		return shim.Error("queryHistoryListByUserIdAndCertificateId query failed" + err.Error())
	}
	var multiCertificateHistoryList,_= getHistoryListResult(it)
	return shim.Success(multiCertificateHistoryList)

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

	type MultiCertificateHistory struct {
		TxId    string   `json:"txId"`
		Value   MultiCertificate   `json:"value"`
	}
	var history []MultiCertificateHistory;
	var multiCertificate MultiCertificate

	defer resultsIterator.Close()
	// buffer is a JSON array containing QueryRecords
	//var buffer bytes.Buffer
	//buffer.WriteString("[")

	//bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		//queryResponse, err := resultsIterator.Next()
		//if err != nil {
		//	return nil, err
		//}
		//// Add a comma before array members, suppress it for the first array member
		//if bArrayMemberAlreadyWritten == true {
		//	buffer.WriteString(",")
		//}
		//item,_:= json.Marshal( queryResponse)
		//buffer.Write(item)
		//bArrayMemberAlreadyWritten = true

		historyData, err := resultsIterator.Next()
		if err != nil {
			return nil,err
		}

		var tx MultiCertificateHistory
		tx.TxId = historyData.TxId                     //copy transaction id over
		json.Unmarshal(historyData.Value, &multiCertificate)     //un stringify it aka JSON.parse()
		if historyData.Value == nil {                  //marble has been deleted
			var emptyMarble MultiCertificate
			tx.Value = emptyMarble                 //copy nil marble
		} else {
			json.Unmarshal(historyData.Value, &multiCertificate) //un stringify it aka JSON.parse()
			tx.Value = multiCertificate                      //copy marble over
		}
		history = append(history, tx)              //add this tx to the list
	}
	//buffer.WriteString("]")
	//fmt.Printf("queryResult:\n%s\n", buffer.String())
	historyAsBytes, _ := json.Marshal(history)
	return historyAsBytes,nil
}