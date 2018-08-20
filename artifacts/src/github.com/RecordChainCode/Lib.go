package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"bytes"
	"fmt"
	"encoding/json"
)

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

	type RecordHistory struct {
		TxId    string   `json:"txId"`
		Value   RecordStruct   `json:"value"`
	}
	var history []RecordHistory;
	var recordStruct RecordStruct

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

		var tx RecordHistory
		tx.TxId = historyData.TxId                     //copy transaction id over
		if historyData.Value == nil {                  //marble has been deleted
			var emptyMarble RecordStruct
			tx.Value = emptyMarble                 //copy nil marble
		} else {
			json.Unmarshal(historyData.Value, &recordStruct) //un stringify it aka JSON.parse()
			tx.Value = recordStruct                      //copy marble over
		}
		history = append(history, tx)              //add this tx to the list
	}
	//buffer.WriteString("]")
	//fmt.Printf("queryResult:\n%s\n", buffer.String())

	historyAsBytes, _ := json.Marshal(history)
	fmt.Printf("queryHistoryResult:\n%s\n", string(historyAsBytes))
	return historyAsBytes,nil
}
