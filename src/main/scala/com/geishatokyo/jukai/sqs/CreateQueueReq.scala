package com.geishatokyo.jukai.sqs

import com.amazonaws.services.sqs.model.{QueueAttributeName, CreateQueueRequest}
import scala.collection.JavaConverters._

/**
 * Attribute keys are defined at
 * com.amazonaws.services.sqs.model.QueueAttributeName
 * User: takeshita
 * DateTime: 12/12/05 18:02
 */
case class CreateQueueReq(queueName : String, attributes : Map[String,String]) {

  def +( kv : (String,String)) = copy(attributes = attributes +(kv))

  def delay(seconds : Int) = {
    this + (QueueAttributeName.DelaySeconds.name() -> seconds.toString)
  }
  def waitTimeout(seconds : Int) = {
    this + (QueueAttributeName.ReceiveMessageWaitTimeSeconds.name() -> seconds.toString)
  }

  def visibilityTimeout( seconds : Int) = {
    this + (QueueAttributeName.VisibilityTimeout.name() -> seconds.toString)
  }

  def withAttributes( attrs : Map[String,String]) = {
    copy(attributes = attributes ++ attrs)
  }


  def toSQSRequest = {
    val req = new CreateQueueRequest(queueName)
    if (attributes.size > 0){
      req.setAttributes(attributes.asJava)
    }
    req
  }

}
