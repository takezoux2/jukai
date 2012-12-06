package com.geishatokyo.jukai.sqs

/**
 * 
 * User: takeshita
 * DateTime: 12/12/06 12:24
 */
object Permission extends Enumeration {

  val SendMessage,ReceiveMessage,DeleteMessage,ChangeMessageVisibility,
  GetQueueAttributes,GetQueueUrl,None = Value

  val All = Value("*")

}
