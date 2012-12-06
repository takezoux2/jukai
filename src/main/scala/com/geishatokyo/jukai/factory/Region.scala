package com.geishatokyo.jukai.factory

import com.amazonaws.auth.{DefaultAWSCredentialsProviderChain, AWSCredentials, AWSCredentialsProvider}
import com.geishatokyo.jukai.simpledb.SimpleDBConnection
import com.geishatokyo.jukai.simpledb.{SimpleDBConnection, SimpleDB}
import com.amazonaws.services.simpledb.AmazonSimpleDBClient
import com.geishatokyo.jukai.util.StringUtil
import com.amazonaws.services.s3.AmazonS3Client
import com.geishatokyo.jukai.s3.S3Connection
import com.amazonaws.services.sqs.AmazonSQSClient
import com.geishatokyo.jukai.sqs.SQSConnection
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient
import com.geishatokyo.jukai.simpleemail.SimpleEmail


/**
 * Check true domains at
 * http://docs.amazonwebservices.com/general/latest/gr/rande.html
 * User: takeshita
 * DateTime: 12/11/01 23:25
 */
class Region[A](
                   endPointDomain : String,
                   credentialsProvider : AWSCredentialsProvider

                   ) {

  def withCredentials( accessKey : String,secretKey : String) : Region[WithCredentials] = {
    new Region[WithCredentials](endPointDomain, SimpleCredentialsProvider(accessKey,secretKey))
  }

  def withCredentials(credentials : AWSCredentials) : Region[WithCredentials] = {
    new Region[WithCredentials](endPointDomain, SimpleCredentialsProvider(credentials))
  }


  def withDefaultCredentials() : Region[WithCredentials] = {
    new Region[WithCredentials](endPointDomain, new DefaultAWSCredentialsProviderChain())
  }


  def simpleDb(implicit WithCredentials : A =:= WithCredentials) = {
      val endpoint = join("sdb",".",endPointDomain)
      val client = new AmazonSimpleDBClient(credentialsProvider)
      client.setEndpoint(endpoint)

    new SimpleDBConnection(client)
  }

  def s3(implicit WithCredentials : A =:= WithCredentials) = {
    val endpoint = {
      if (endPointDomain == "amazonaws.com") "s3." + endPointDomain
      else join("s3","-",endPointDomain)
    }
    val client = new AmazonS3Client(credentialsProvider)
    client.setEndpoint(endpoint)
    new S3Connection(client)
  }

  def sqs(implicit WithCredentials : A =:= WithCredentials) = {
    val endpoint = join("sqs",".",endPointDomain)
    val client = new AmazonSQSClient(credentialsProvider)
    client.setEndpoint(endpoint)
    new SQSConnection(client)

  }

  def simpleMail(implicit WithCredentials : A =:= WithCredentials) = {
    val endpoint = "email.us-east-1.amazonaws.com"
    val client = new AmazonSimpleEmailServiceClient(credentialsProvider)
    client.setEndpoint(endpoint)
    new SimpleEmail(client)
  }
  def simpleMail_smtp(implicit WithCredentials : A =:= WithCredentials) = {
    val endpoint = "email-smtp.us-east-1.amazonaws.com:465"
    val client = new AmazonSimpleEmailServiceClient(credentialsProvider)
    client.setEndpoint(endpoint)
    new SimpleEmail(client)

  }


  private def join( serviceName : String ,separator : String, endPointDomain : String) = {
    serviceName + separator + endPointDomain.stripPrefix(separator)
  }

}

final class WithCredentials

object Region{

  def apply(endPointDomain : String) : Region[Any] = {
    new Region(endPointDomain,null)
  }

  val NorthernVirginia  = apply("amazonaws.com")
  val Oregon  = apply("us-west-2.amazonaws.com")
  val NorthernCalifornia  = apply("us-west-1.amazonaws.com")
  val Ireland  = apply("eu-west-1.amazonaws.com")
  val Singapore = apply("ap-southeast-1.amazonaws.com")
  val Tokyo = apply("ap-northeast-1.amazonaws.com")
  val SaoPaulo = apply("sa-east-1.amazonaws.com")

  val nameMap = Map(
    "northernvirginia" -> NorthernVirginia,
    "northern-virginia" -> NorthernVirginia,
    "virginia" -> NorthernVirginia,
    "us-east" -> NorthernVirginia,
    "oregon" -> Oregon,
    "us-west" -> Oregon,
    "us-west-1" -> Oregon,
    "california" -> NorthernCalifornia,
    "us-west-2" -> NorthernCalifornia,
    "tokyo" -> Tokyo,
    "japan" -> Tokyo,
    "ap-northeast" -> Tokyo,
    "singapore" -> Singapore,
    "ap-southeast" -> Singapore,
    "ireland" -> Ireland,
    "eu-west" -> Ireland,
    "saopaulo" -> SaoPaulo,
    "brazil" -> SaoPaulo,
    "sa-east" -> SaoPaulo)

  def fromName( name : String) = {
    nameMap.get(name.toLowerCase) orElse
    nameMap.get(StringUtil.camelToHyphen(name)) get
  }

}


