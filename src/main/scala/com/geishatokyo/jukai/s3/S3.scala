package com.geishatokyo.jukai.s3

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import scala.collection.JavaConverters._
import com.geishatokyo.jukai.util.IOUtil
import com.amazonaws.services.s3.model._
import java.io.{ByteArrayInputStream, InputStream}
import scala.Left
import scala.Some
import scala.Right
import com.amazonaws.AmazonServiceException

/**
 * Created with IntelliJ IDEA.
 * User: takezoux3
 * Date: 12/12/04
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
class S3(val client : AmazonS3,bucketName : String) extends scala.collection.mutable.Map[String,Array[Byte]]{

  var metadataGenerator : MetadataGenerator = DefaultMetadataGenerator

  var defaultACL : Option[Either[CannedAccessControlList,AccessControlList]] = Some(Left(CannedAccessControlList.PublicRead))

  def setDefaultACL( v : CannedAccessControlList) = {
    defaultACL = Some(Left(v))
  }

  def setDefaultACL( v : AccessControlList) = {
    defaultACL = Some(Right(v))
  }



  // Map operations

  def +=(kv: (String, Array[Byte])) = {
    putObject(kv._1,kv._2)
    this
  }

  def -=(key: String) = {
    deleteObject(key)
    this
  }

  def get(key: String): Option[Array[Byte]] = {

    tryCatchNoSuchKeyException{
      getObjectSafely(key)( s3Object => {
        IOUtil.readStream(s3Object.getObjectContent)
      })
    }

  }

  private def tryCatchNoSuchKeyException[T]( func : => T) : Option[T] = {
    try {
      Some(func)
    }catch{
      case e : AmazonS3Exception => {
        if(e.getErrorType == AmazonServiceException.ErrorType.Client &&
          e.getErrorCode == "NoSuchKey"){
          None
        }else{
          // other reason
          throw e
        }
      }
    }
  }


  def iterator: Iterator[(String, Array[Byte])] = {
    listObjects().toKeyAndValues
  }



  // Bucket operations

  def createBucket() = {
    client.createBucket(bucketName)
  }

  def createBucketIfNotExists() = {
    if (!existsBucket()){
      createBucket()
      true
    }else{
      false
    }
  }

  def existsBucket() = {
    client.doesBucketExist(bucketName)
  }

  def deleteBucket() = {
    client.deleteBucket(bucketName)
  }

  def getS3AccountOwner() = {
    client.getS3AccountOwner
  }

  def getBucketACL() = {
    client.getBucketAcl(bucketName)
  }

  def setBucketACL(control : CannedAccessControlList) = {
    client.setBucketAcl(bucketName,control)
  }
  def setBucketACL(control : AccessControlList) = {
    client.setBucketAcl(bucketName,control)
  }


  // Object operations

  /**
   * You must close stream manually.
   * If you want to use S3Object, please use getObjectSafely instead.
   * @param key
   * @return
   */
  @deprecated
  def getObject(key  : String) = {
    client.getObject(bucketName,key)
  }

  /**
   *
   * @param key
   * @param func
   * @tparam T
   * @return
   */
  def getObjectSafely[T](key : String)(func : S3Object => T) : T = {
    val s3Object = client.getObject(bucketName,key)
    try{
      func(s3Object)
    }finally{
      IOUtil.closeQuietly(s3Object.getObjectContent)
    }
  }

  def getMetadata(key : String) : Option[ObjectMetadata] = tryCatchNoSuchKeyException {
    client.getObjectMetadata(bucketName,key)
  }

  def getACL(key : String) : Option[AccessControlList] = tryCatchNoSuchKeyException{
    client.getObjectAcl(bucketName,key)
  }


  def putObject(key : String, data : Array[Byte]) : PutObjectResult = {
    val metadata = metadataGenerator.generateMetadata(key,data)

    putObject(key , new ByteArrayInputStream(data),metadata)
  }


  def putObject(key : String, inputStream : InputStream, metadata : ObjectMetadata) : PutObjectResult = {
    val response = client.putObject(bucketName,key,inputStream,metadata)
    defaultACL match{
      case None =>
      case Some(Left(cannedAccessControlList)) => {
        setACL(key,cannedAccessControlList)
      }
      case Some(Right(accessControlList)) => {
        setACL(key,accessControlList)
      }
    }
    response
  }


  def setACL(key : String, control : CannedAccessControlList) = {
    client.setObjectAcl(bucketName,key,control)
  }
  def setACL(key : String, control : AccessControlList) = {
    client.setObjectAcl(bucketName,key,control)
  }


  def deleteObject(key : String) = {
    client.deleteObject(bucketName,key)
  }

  class Paging( list : ObjectListing, autoLoad : Boolean) extends Iterable[S3ObjectSummary]{
    def iterator = new S3ObjectSummaryIterator(list,autoLoad)

    def toKeys : Iterator[String] = {
      iterator.map( s => s.getKey)
    }

    def toKeyAndValues : Iterator[(String,Array[Byte])] = {

      iterator.map( s => s.getKey -> apply(s.getKey))
    }
  }

  val DefaultMaxCount = 1000// Magic number

  class S3ObjectSummaryIterator(private var list : ObjectListing, autoLoad : Boolean) extends Iterator[S3ObjectSummary]{
    private var currentIterator = list.getObjectSummaries.asScala.toIterator

    def hasNext = {
      if(currentIterator.hasNext){
        true
      }else{
        if(autoLoad && list.isTruncated){
          // load next list
          list = client.listNextBatchOfObjects(list)
          currentIterator = list.getObjectSummaries().asScala.toIterator
          currentIterator.hasNext
        }else{
          false
        }
      }
    }

    def next() = {
      currentIterator.next()
    }
  }



  def listObjects() : Paging = {
    val list = client.listObjects(bucketName)
    new Paging(list,true)
  }
  def listObjects(prefix : String) : Paging = {
    val list = client.listObjects(bucketName,prefix)
    new Paging(list,true)
  }
  def listObjects(prefix : String,maxCount : Int) : Paging= {
    listObjects(ListObjectReq(Some(prefix),None,Some(maxCount)))
  }

  def listObjects( request : ListObjectReq ) : Paging = {
    val req = request.toS3Request(bucketName)
    val list = client.listObjects(req)
    new Paging(list,!request.maxKeyCount.isDefined)
  }

  /**
   * If there are
   * <pre>
   *   hoge/fuga/data1.jpg
   *   hoge/data2.jpg
   *   hoge/aaa/data3.jpg
   *   hoge_data.jpg
   * </pre>
   * keys and you pass prefix="hoge" and delimiter="hoge/",
   * Paging=["hoge_data.jpg"],commonPrefixes=["hoge/","hoge/fuga/","hoge/aaa/"] will be returned.
   *
   *
   * @param request
   * @return (files,commonPrefixes)
   */

  def listObjects(request : ListUpReqWithDelimiter) : (Paging,List[String]) = {
    val req = request.toS3Request(bucketName)
    val list = client.listObjects(req)
    (new Paging(list,!request.maxKeyCount.isDefined), list.getCommonPrefixes().asScala.toList )

  }



}
