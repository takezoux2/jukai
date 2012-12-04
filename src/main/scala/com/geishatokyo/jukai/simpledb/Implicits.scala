package com.geishatokyo.jukai.simpledb

import scala.collection.JavaConverters._
import com.amazonaws.services.simpledb.model._

/**
 * 
 * User: takeshita
 * DateTime: 12/11/01 22:46
 */
object Implicits {

  implicit def strToGetRequest(k : String) = {
    GetRequest(k)
  }
  implicit def strToDeleteRequest(k : String) = {
    DeleteRequest(k)
  }
  implicit def tupleToGetRequest(k : (String,String)) = {
    GetRequest(k._1,k._2)
  }

  implicit def tupleWithListToGetRequest(k : (String,Seq[String])) = {
    GetRequest(k._1,k._2:_*)
  }

  implicit def tupleWithMapToPutRequest(k : (String,Map[String,String])) = {
    PutRequest(k._1,k._2)
  }

  /**
   * Mapping such to request
   * "a" -> "b" -> "c"
   * @param v
   * @return
   */
  implicit def tupleTupleToPutRequest(v : ((String,String),String)) = {
    PutRequest(v._1._1,Map(v._1._2 -> v._2))
  }


  implicit def getRequestToDeleteRequest(r : GetRequest) = DeleteRequest(r)

  implicit def strToKeyWrapper( k : String) = {
    new KeyWrapper(k)
  }
  class KeyWrapper(k : String) {


    def ~>(values : (String,String)*) = {
      PutRequest(k,values.toMap)
    }

    def ~>( attributeNames : String*) = {
      GetRequest(k,attributeNames:_*)
    }
  }





  implicit def attributesToMap( attributes : java.util.Collection[Attribute]) = {
    attributes.asScala.map(a => {
      a.getName -> a.getValue
    }).toMap
  }

  def toReplaceableAttributes( m : Map[String,String],replace_? : Boolean) = {
    m.map(p => {
      new ReplaceableAttribute(p._1,p._2,replace_?)
    }).toList.asJava
  }

  def toUpdateCondition(exp : Exp) = {
    exp match{
      case Equals(k,v) => new UpdateCondition().withName(k).withValue(v)
      case Exists(k) => new UpdateCondition().withName(k).withExists(true)
    }
  }

  def toReplaceableItem( r : PutRequest) = {
    new ReplaceableItem(r.key, toReplaceableAttributes(r.values,r.replace_?))
  }

}

