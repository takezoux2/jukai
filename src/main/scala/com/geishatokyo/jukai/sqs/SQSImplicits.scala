package com.geishatokyo.jukai.sqs

/**
 * 
 * User: takeshita
 * DateTime: 12/12/05 17:22
 */
object SQSImplicits {

  implicit def toCreateQueueReq(queueName : String) = CreateQueueReq(queueName,Map.empty)
  implicit def toSendMessageReq(message : String) = SendMessageReq(message,None)

}
