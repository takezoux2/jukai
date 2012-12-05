package com.geishatokyo.jukai.simpledb

/**
 * 
 * User: takeshita
 * DateTime: 12/11/02 0:06
 */
case class GetReq(key : String, attributes : Seq[String],consistentRead_? : Boolean) {

  def +( v : String) = GetReq(key, attributes.++(Seq(v)),consistentRead_?)

  def @@( vs : String*) = GetReq(key, attributes.++(vs),consistentRead_?)

  def consistently = GetReq(key, attributes,true)


}

object GetReq{

  def apply(key : String) : GetReq = {
    new GetReq(key,Nil,false)
  }
  def apply(key : String,attributes : String*) : GetReq = {
    new GetReq(key,attributes,false)
  }
}
