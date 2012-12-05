package com.geishatokyo.jukai.s3

import com.amazonaws.services.s3.model.ListObjectsRequest

/**
 * 
 * User: takeshita
 * DateTime: 12/12/05 13:15
 */
case class ListObjectReq(prefix : Option[String],markerKey : Option[String],maxKeyCount : Option[Int]) {

  def max( v : Int) = this.copy(maxKeyCount = Some(v))
  def from( key : String) = this.copy(markerKey = Some(key))
  def delimitedBy(delimiter : String) = ListUpReqWithDelimiter(prefix,markerKey,maxKeyCount,delimiter)
  def prefix( p : String) = this.copy(prefix = Some(p))

  // SQL Like

  def limit(v : Int) = this.copy(maxKeyCount = Some(v))

  def toS3Request(bucket : String) = {
    val req = new ListObjectsRequest()
    req.setBucketName(bucket)
    prefix.foreach(req.setPrefix(_))
    markerKey.foreach(req.setMarker(_))
    maxKeyCount.foreach(req.setMaxKeys(_))
    req
  }

}

case class ListUpReqWithDelimiter(prefix : Option[String],markerKey : Option[String],maxKeyCount : Option[Int], delimiter : String){

  def max( v : Int) = this.copy(maxKeyCount = Some(v))
  def from( key : String) = this.copy(markerKey = Some(key))
  def delimitedBy(delimiter : String) = copy(delimiter = delimiter)
  def prefix( p : String) = this.copy(prefix = Some(p))


  // SQL Like

  def limit(v : Int) = this.copy(maxKeyCount = Some(v))

  def toS3Request(bucket : String) = {
    val req = new ListObjectsRequest()
    req.setBucketName(bucket)
    prefix.foreach(req.setPrefix(_))
    markerKey.foreach(req.setMarker(_))
    maxKeyCount.foreach(req.setMaxKeys(_))
    req.setDelimiter(delimiter)
    req
  }
}
