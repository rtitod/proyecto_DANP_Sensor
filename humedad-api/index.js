const AWS = require('aws-sdk');
AWS.config.update( {
  region: 'us-west-2'
});
const dynamodb = new AWS.DynamoDB.DocumentClient();
const dynamoTableName ='humedad-tabla';
const sensorPath ='/sensorapi';

exports.handler = async function(event) {
    console.log('Request event: ', event);
    let response;
    switch(true){
        case event.httpMethod === 'GET' && event.path ===sensorPath:
            response = getRegisters(event.queryStringParameters.startregister,event.queryStringParameters.maxregisters);
            break;   
        case event.httpMethod === 'POST' && event.path ===sensorPath:
            response = await saveRegister(JSON.parse(event.body))
            break;   
        
    }
    return response;
}

function buildResponse(statusCode, body){
    return{
        statusCode: statusCode,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    }
}

async function getRegisters(startregister,maxregisters){
    const params = {
        TableName:dynamoTableName
    }
    const lastRegistroId = { value: 0 };
    const allRegs = await scanDynamoRecords(params,[],startregister,maxregisters, lastRegistroId);
    const body = {
        registers: allRegs,
        start:parseInt(startregister),
        max:parseInt(maxregisters),
        lastRegistroId: lastRegistroId.value,
    }
    return buildResponse(200,body);
}

async function scanDynamoRecords(scanParams, itemArray, startregister, maxregisters, lastRegistroId) {
  try {
    const dynamoData = await dynamodb.scan(scanParams).promise();
    itemArray = itemArray.concat(dynamoData.Items);

    if (dynamoData.LastEvaluateKey) {
      scanParams.ExclusiveStartKey = dynamoData.LastEvaluateKey;
      return await scanDynamoRecords(scanParams, itemArray, startregister, maxregisters, lastRegistroId);
    } else {
      const sortedArray = itemArray.sort((a, b) => {
        if (a.RegistroId < b.RegistroId) {
          return -1;
        } else if (a.RegistroId > b.RegistroId) {
          return 1;
        } else {
          return 0;
        }
      });
      
      const tmpArray = sortedArray.reverse();
      
      lastRegistroId.value = tmpArray.length > 0 ? tmpArray[0].RegistroId : 0;

      const startIndex = startregister - 1;

      const resultArray = tmpArray.slice(startIndex);

      return resultArray.slice(0, maxregisters);
    }
  } catch (error) {
    console.error('error on returning ', error);
  }
}

async function saveRegister(requestbody){
    const params = {
        TableName: dynamoTableName,
        Item:requestbody,
        ConditionExpression: "RegistroId <> :RegistroIdVal",
        ExpressionAttributeValues: {
            ":RegistroIdVal" : requestbody.RegistroId
        }
    }
    return await dynamodb.put(params).promise().then(() =>{
        const body = {
            Operation: 'SAVE',
            Message: 'Sucess',
            Item: requestbody
        }
        return buildResponse(200,body)
    }, (error) => {
        console.error('error on saving ', error);
        
    })
    
}
