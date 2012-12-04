package com.geishatokyo.jukai.simpledb

/**
 * 
 * User: takeshita
 * DateTime: 12/11/02 16:21
 */
case class DeleteRequest(key : String,attributes : Seq[String], exp : Option[Exp]) {

  def +( v : String) = DeleteRequest(key, attributes.++(Seq(v)),exp)

  def @@( vs : String*) = DeleteRequest(key, attributes.++(vs),exp)

  def expecting(exp : Exp) = DeleteRequest(key,attributes,Some(exp))


}


object DeleteRequest{

  def apply(key : String) : DeleteRequest = {
    new DeleteRequest(key,Nil,None)
  }
  def apply(key : String,attributes : String*) : DeleteRequest = {
    new DeleteRequest(key,attributes,None)
  }

  def apply(getReq : GetRequest) : DeleteRequest = {
    DeleteRequest(getReq.key,getReq.attributes,None)
  }
}
