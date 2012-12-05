package com.geishatokyo.jukai.sqs

import com.amazonaws.services.sqs.AmazonSQS
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.model.{DeleteMessageRequest, ReceiveMessageRequest, Message, ListQueuesRequest}

/**
 * 
 * User: takeshita
 * DateTime: 12/12/05 16:45
 */
class SQS(client : AmazonSQS,queueUrl : String) {


  def sendMessage( sendMessageReq : SendMessageReq) = {
    val r = client.sendMessage(sendMessageReq.toSQSRequest(queueUrl))
    r
  }

  def receiveAMessage() : Message = {
    receiveMessage(ReceiveMessageReq().fetchCount(1))(0)
  }

  def receiveMessage() : List[Message] = {
    receiveMessage(ReceiveMessageReq())
  }
  def receiveMessage(waitTimeout : Int) : List[Message] = {
    receiveMessage(ReceiveMessageReq().waitTimeout(waitTimeout))
  }

  def receiveMessage( receiveMessageReq : ReceiveMessageReq) : List[Message] = {
    val r = client.receiveMessage(receiveMessageReq.toSQLRequest(queueUrl))
    r.getMessages.asScala.toList
  }

  def deleteMessage( message : Message) : Boolean = {
    deleteMessage(message.getReceiptHandle)
  }

  def deleteMessage( receiptHandle : String) : Boolean = {
    val req = new DeleteMessageRequest(queueUrl,receiptHandle)
    client.deleteMessage(req)
    true
  }



  def processMessage[T](receiveMessageReq : ReceiveMessageReq, func : Message => T) : List[T] = {
    val messages = receiveMessage(receiveMessageReq)
    val results = messages.map( m => func(m))
    messages.foreach(deleteMessage(_))
    results
  }




  def shutdown() = {
    client.shutdown()
  }


}
