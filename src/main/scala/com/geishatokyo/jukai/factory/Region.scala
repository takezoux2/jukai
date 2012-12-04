package com.geishatokyo.pentagon.util.aws.factory

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider}
import com.geishatokyo.jukai.simpledb.SimpleDBConnection
import com.geishatokyo.jukai.simpledb.{SimpleDBConnection, SimpleDB}
import com.geishatokyo.jukai.SimpleCredentialsProvider
import com.amazonaws.services.simpledb.AmazonSimpleDBClient


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


  def simpleDb(implicit WithCredentials : A =:= WithCredentials) = {
      val endpoint = join("sdb",".",endPointDomain)
      val client = new AmazonSimpleDBClient(credentialsProvider)
      client.setEndpoint(endpoint)

    new SimpleDBConnection(client)
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
    nameMap(name.toLowerCase)
  }

}

