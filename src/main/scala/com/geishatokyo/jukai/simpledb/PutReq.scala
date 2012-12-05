package com.geishatokyo.jukai.simpledb

/**
 *
 * User: takeshita
 * DateTime: 12/11/01 22:50
 */
case class PutReq ( key : String, values : Map[String,String], exp : Option[Exp], replace_? : Boolean){

  def expecting( exp : Exp) = {
    PutReq(key,values,Some(exp),replace_?)
  }

  def when(exp : Exp) = expecting(exp)

  def +( v : (String,String)) = {
    PutReq(key , values + v,exp,replace_?)
  }

  def onlyReplace = {
    PutReq(key,values,exp,true)
  }
}

object PutReq{

  def apply( key : String) : PutReq = {
    PutReq(key,Map.empty,None,false)
  }

  def apply(key : String, values : Map[String,String]) : PutReq = {
    PutReq(key,values,None,false)
  }
}

trait Exp

case class Equals(attributeName : String, value : String) extends Exp
case class Exists(attributeName : String) extends Exp