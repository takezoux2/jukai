package com.geishatokyo.jukai.sqs

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.{GetQueueUrlRequest, ListQueuesRequest}
import scala.collection.JavaConverters._

/**
 * 
 * User: takeshita
 * DateTime: 12/12/05 17:56
 */
class SQSConnection(client : AmazonSQS) {

  def createQueue( req : CreateQueueReq) : String = {
    val r = client.createQueue(req.toSQSRequest)
    r.getQueueUrl
  }

  def listQueues() : List[String] = {
    client.listQueues().getQueueUrls.asScala.toList
  }
  def listQueues(prefix : String) = {
    client.listQueues(new ListQueuesRequest(prefix)).getQueueUrls.asScala.toList
  }


  def queueFromName(queueName : String) = {
    val queue = client.getQueueUrl(new GetQueueUrlRequest(queueName))
    new SQS(client,queue.getQueueUrl)
  }

  def queueFromUrl(queueUrl : String) = {
    new SQS(client,queueUrl)
  }


}
