package com.geishatokyo.jukai.simpledb

/**
 * 
 * User: takeshita
 * DateTime: 12/11/02 16:21
 */
case class DeleteReq(key : String,attributes : Seq[String], exp : Option[Exp]) {

  def +( v : String) = DeleteReq(key, attributes.++(Seq(v)),exp)

  def @@( vs : String*) = DeleteReq(key, attributes.++(vs),exp)

  def expecting(exp : Exp) = DeleteReq(key,attributes,Some(exp))


}


object DeleteReq{

  def apply(key : String) : DeleteReq = {
    new DeleteReq(key,Nil,None)
  }
  def apply(key : String,attributes : String*) : DeleteReq = {
    new DeleteReq(key,attributes,None)
  }

  def apply(getReq : GetReq) : DeleteReq = {
    DeleteReq(getReq.key,getReq.attributes,None)
  }
}
