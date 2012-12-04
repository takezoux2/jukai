package com.geishatokyo.jukai.s3

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.s3.{AmazonS3Client, AmazonS3}
import com.amazonaws.services.s3.model.Bucket
import scala.collection.JavaConverters._
import java.io.Closeable

/**
 * Created with IntelliJ IDEA.
 * User: takezoux3
 * Date: 12/12/04
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
class S3Connection(client : AmazonS3) {

  def apply(bucketName : String) = {
    bucket(bucketName)
  }

  def bucket(bucketName : String) = {
    new S3(client,bucketName)
  }



  def listBuckets() : List[Bucket] = {
    val buckets = client.listBuckets()
    buckets.asScala.toList
  }

  def listBucketNames() : List[String] = {
    listBuckets().map(_.getName)
  }


  def shutdown() = {
    client match{
      case c : AmazonS3Client => c.shutdown()
      case c : Closeable => c.close()
      case _ =>
    }
  }


}
