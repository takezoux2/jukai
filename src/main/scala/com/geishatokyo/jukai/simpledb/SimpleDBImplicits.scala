package com.geishatokyo.jukai.simpledb

import scala.collection.JavaConverters._
import com.amazonaws.services.simpledb.model._

/**
 * 
 * User: takeshita
 * DateTime: 12/11/01 22:46
 */
object SimpleDBImplicits {

  implicit def strToGetRequest(k : String) = {
    GetReq(k)
  }
  implicit def strToDeleteRequest(k : String) = {
    DeleteReq(k)
  }
  implicit def tupleToGetRequest(k : (String,String)) = {
    GetReq(k._1,k._2)
  }

  implicit def tupleWithListToGetRequest(k : (String,Seq[String])) = {
    GetReq(k._1,k._2:_*)
  }

  implicit def tupleWithMapToPutRequest(k : (String,Map[String,String])) = {
    PutReq(k._1,k._2)
  }

  /**
   * Mapping such to request
   * "a" -> "b" -> "c"
   * @param v
   * @return
   */
  implicit def tupleTupleToPutRequest(v : ((String,String),String)) = {
    PutReq(v._1._1,Map(v._1._2 -> v._2))
  }


  implicit def getRequestToDeleteRequest(r : GetReq) = DeleteReq(r)

  implicit def strToKeyWrapper( k : String) = {
    new KeyWrapper(k)
  }
  class KeyWrapper(k : String) {


    def ~>(values : (String,String)*) = {
      PutReq(k,values.toMap)
    }

    def ~>( attributeNames : String*) = {
      GetReq(k,attributeNames:_*)
    }

    def ===(value : String) = {
      Equals(k,value)
    }

    def exist = {
      Exists(k)
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

  def toReplaceableItem( r : PutReq) = {
    new ReplaceableItem(r.key, toReplaceableAttributes(r.values,r.replace_?))
  }

}

