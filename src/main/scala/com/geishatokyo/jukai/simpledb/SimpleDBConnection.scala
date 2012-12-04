package com.geishatokyo.jukai.simpledb

import com.amazonaws.services.simpledb.{AmazonSimpleDB, AmazonSimpleDBClient}
import com.amazonaws.auth.AWSCredentialsProvider
import scala.collection.JavaConverters._
import java.io.Closeable

/**
 * 
 * User: takeshita
 * DateTime: 12/11/01 22:18
 */
class SimpleDBConnection( client : AmazonSimpleDB) {

  def domain( domainName : String) : SimpleDB = {
    new SimpleDB(client,domainName)
  }


  def domains : List[String] = {
    client.listDomains().getDomainNames().asScala.toList
  }

  def shutdown() = {
    client match{
      case c : AmazonSimpleDBClient => c.shutdown()
      case c : Closeable => c.close()
      case _ =>
    }
  }

}
