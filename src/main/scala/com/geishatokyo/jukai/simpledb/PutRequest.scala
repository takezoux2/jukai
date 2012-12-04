package com.geishatokyo.jukai.simpledb

/**
 *
 * User: takeshita
 * DateTime: 12/11/01 22:50
 */
case class PutRequest ( key : String, values : Map[String,String], exp : Option[Exp], replace_? : Boolean){

  def expecting( exp : Exp) = {
    PutRequest(key,values,Some(exp),replace_?)
  }

  def when(exp : Exp) = expecting(exp)

  def +( v : (String,String)) = {
    PutRequest(key , values + v,exp,replace_?)
  }

  def onlyReplace = {
    PutRequest(key,values,exp,true)
  }
}

object PutRequest{

  def apply( key : String) : PutRequest = {
    PutRequest(key,Map.empty,None,false)
  }

  def apply(key : String, values : Map[String,String]) : PutRequest = {
    PutRequest(key,values,None,false)
  }
}

trait Exp

case class Equals(attributeName : String, value : String) extends Exp
case class Exists(attributeName : String) extends Exp