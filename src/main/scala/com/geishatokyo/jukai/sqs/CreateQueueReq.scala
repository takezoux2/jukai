package com.geishatokyo.jukai.sqs

import com.amazonaws.services.sqs.model.CreateQueueRequest
import scala.collection.JavaConverters._

/**
 * 
 * User: takeshita
 * DateTime: 12/12/05 18:02
 */
case class CreateQueueReq(queueName : String, attributes : Map[String,String]) {

  def toSQSRequest = {
    val req = new CreateQueueRequest(queueName)
    if (attributes.size > 0){
      req.setAttributes(attributes.asJava)
    }
    req
  }

}
