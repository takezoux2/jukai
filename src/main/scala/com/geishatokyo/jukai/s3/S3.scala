package com.geishatokyo.jukai.s3

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import scala.collection.JavaConverters._
import com.geishatokyo.jukai.{AWSResult, AWSSuccess, IOUtil}
import com.amazonaws.services.s3.model.{PutObjectResult, AccessControlList, CannedAccessControlList, ObjectMetadata}
import java.io.{ByteArrayInputStream, InputStream}

/**
 * Created with IntelliJ IDEA.
 * User: takezoux3
 * Date: 12/12/04
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
class S3(client : AmazonS3,bucketName : String) extends scala.collection.mutable.Map[String,Array[Byte]]{

  var metadataGenerator : MetadataGenerator = DefaultMetadataGenerator

  var defaultACL : Option[Either[CannedAccessControlList,AccessControlList]] = Some(Left(CannedAccessControlList.PublicRead))

  def setDefaultACL( v : CannedAccessControlList) = {
    defaultACL = Some(Left(v))
  }

  def setDefaultACL( v : AccessControlList) = {
    defaultACL = Some(Right(v))
  }





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


  def +=(kv: (String, Array[Byte])) = {
    putObject(kv._1,kv._2)
    this
  }

  def -=(key: String) = null

  def get(key: String): Option[Array[Byte]] = {
    val s3Object = getObject(key)
    if (s3Object != null){
      val data = IOUtil.readStream(s3Object.getObjectContent)
      s3Object.getObjectContent.close()
      Some(data)
    }else{
      None
    }
  }

  def iterator: Iterator[(String, Array[Byte])] = null


  def getObject(key  : String) = {
    client.getObject(bucketName,key)
  }

  def putObject(key : String, data : Array[Byte]) : AWSResult[String,PutObjectResult]  = {
    val metadata = metadataGenerator.generateMetadata(key,data)

    putObject(key , new ByteArrayInputStream(data),metadata)
  }

  def putObject(key : String, inputStream : InputStream, metadata : ObjectMetadata) : AWSResult[String,PutObjectResult] = {
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
    AWSSuccess(response.getVersionId,response)
  }

  def setACL(key : String, control : CannedAccessControlList) = {
    client.setObjectAcl(bucketName,key,control)
  }
  def setACL(key : String, control : AccessControlList) = {
    client.setObjectAcl(bucketName,key,control)
  }

}
