package com.geishatokyo.jukai.sqs

import com.amazonaws.services.sqs.AmazonSQS
import scala.collection.JavaConverters._
import com.amazonaws.services.sqs.model._
import actors.threadpool.TimeoutException

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
    val m = receiveMessage(ReceiveMessageReq().fetchCount(1))
    if (m.isDefinedAt(0)){
      m(0)
    }else{
      throw new TimeoutException("Time out to receive message from SQS(%s)".format(queueUrl))
    }
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

  def deleteMessages(messages : List[Message]) : (List[String],List[String]) = {
    val r = client.deleteMessageBatch(new DeleteMessageBatchRequest(queueUrl,
      messages.map(m => {
        new DeleteMessageBatchRequestEntry(m.getMessageId,m.getReceiptHandle)
      }).asJava))

    r.getSuccessful().asScala.toList.map(_.getId) -> r.getFailed.asScala.toList.map(_.getId)
  }




  def processMessage[T](receiveMessageReq : ReceiveMessageReq, func : Message => T) : List[T] = {
    val messages = receiveMessage(receiveMessageReq)
    val results = messages.map( m => func(m))
    deleteMessages(messages)
    results
  }





  def shutdown() = {
    client.shutdown()
  }


}
