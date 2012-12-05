package com.geishatokyo.jukai.sqs

import com.amazonaws.services.sqs.model.SendMessageRequest

/**
 * 
 * User: takeshita
 * DateTime: 12/12/05 18:35
 */
case class SendMessageReq( messageBody : String, delaySecs : Option[Int] ) {

  def delaySecs(secs : Int) = this.copy(delaySecs = Some(secs))


  def toSQSRequest(queueUrl : String) = {
    val req = new SendMessageRequest(queueUrl,messageBody)
    delaySecs.foreach(req.setDelaySeconds(_))
    req
  }



}
