package com.geishatokyo.jukai.sqs

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.{DeleteQueueRequest, GetQueueUrlRequest, ListQueuesRequest}
import scala.collection.JavaConverters._
import com.amazonaws.AmazonServiceException
import com.amazonaws.AmazonServiceException.ErrorType

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

  def exists(queueName : String) = {
    val queue = client.getQueueUrl(new GetQueueUrlRequest(queueName))
    true
  }

  def queueFromName(queueName : String,autoCreate : Boolean = false) = {
    getQueueUrl(queueName) match{
      case Some(url) => new SQS(client,url)
      case None => {
        if (autoCreate){
          val url = createQueue(CreateQueueReq(queueName,Map.empty))
          new SQS(client,url)
        }else{
          throw new Exception("Queue:%s not found".format(queueName))
        }
      }
    }
  }

  private def getQueueUrl(queueName : String) : Option[String] = {
    try{
      Some(client.getQueueUrl(new GetQueueUrlRequest(queueName)).getQueueUrl)
    }catch{
      case e : AmazonServiceException => {
        if (e.getErrorType == ErrorType.Client){
          None
        }else{
          throw e
        }
      }
    }
  }

  def queueFromUrl(queueUrl : String) = {
    new SQS(client,queueUrl)
  }


  def deleteQueueByName(queueName : String) : Boolean = {
    val url = client.getQueueUrl(new GetQueueUrlRequest(queueName))
    deleteQueueByUrl(url.getQueueUrl)
  }
  def deleteQueueByUrl(queueUrl : String) : Boolean = {
    client.deleteQueue(new DeleteQueueRequest(queueUrl))
    true
  }


  def shutdown() = client.shutdown()



}
