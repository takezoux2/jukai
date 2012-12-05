package com.geishatokyo.jukai.sqs

import com.amazonaws.services.sqs.model.ReceiveMessageRequest

/**
 * 
 * User: takeshita
 * DateTime: 12/12/05 18:48
 */
case class ReceiveMessageReq(fetchCount : Option[Int],
                             visibilityTimeoutSeconds : Option[Int],
                             waitTimeoutSeconds : Option[Int]) {

  def fetchCount(i : Int) = this.copy(fetchCount = Some(i))
  def visibilityTimeout(secs : Int) = this.copy(visibilityTimeoutSeconds = Some(secs))
  def waitTimeout(secs : Int) = this.copy(waitTimeoutSeconds = Some(secs))

  def toSQLRequest(queueUrl : String) = {
    val req = new ReceiveMessageRequest(queueUrl)
    fetchCount.foreach(req.setMaxNumberOfMessages(_))
    visibilityTimeoutSeconds.foreach(req.setVisibilityTimeout(_))
    waitTimeoutSeconds.foreach(req.setWaitTimeSeconds(_))
    req
  }


}

object ReceiveMessageReq{

  def apply() : ReceiveMessageReq = {
    ReceiveMessageReq(None,None,None)
  }
}
