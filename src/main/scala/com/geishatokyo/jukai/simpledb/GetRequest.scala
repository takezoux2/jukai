package com.geishatokyo.jukai.simpledb

/**
 * 
 * User: takeshita
 * DateTime: 12/11/02 0:06
 */
case class GetRequest(key : String, attributes : Seq[String],consistentRead_? : Boolean) {

  def +( v : String) = GetRequest(key, attributes.++(Seq(v)),consistentRead_?)

  def @@( vs : String*) = GetRequest(key, attributes.++(vs),consistentRead_?)

  def consistently = GetRequest(key, attributes,true)


}

object GetRequest{

  def apply(key : String) : GetRequest = {
    new GetRequest(key,Nil,false)
  }
  def apply(key : String,attributes : String*) : GetRequest = {
    new GetRequest(key,attributes,false)
  }
}
