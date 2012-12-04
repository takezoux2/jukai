package com.geishatokyo.jukai.simpledb

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.simpledb._
import com.amazonaws.services.simpledb.model._
import com.geishatokyo.jukai._
import scala.collection.JavaConverters._
import com.amazonaws.AmazonServiceException
import org.slf4j.LoggerFactory
import java.io.Closeable

/**
 *
 * User: takeshita
 * DateTime: 12/11/01 22:18
 */
class SimpleDB(syncClient : AmazonSimpleDB,val domain : String) {

  /**
   * 厳密にパラメーターをチェックする
   */
  var rigidCheck = false
  val logger = LoggerFactory.getLogger(classOf[SimpleDB])

  def createDomain() = {
    val request = new CreateDomainRequest(domain)
    syncClient.createDomain(request)
    true

  }

  def existsDomain_?() = {
    val request = new ListDomainsRequest().withNextToken(domain).withMaxNumberOfDomains(100)
    val r = syncClient.listDomains(request)
    r.getDomainNames.size() > 0 && r.getDomainNames.asScala.exists(_ == domain)
  }

  def createDomainIfNotExists() = {
    if (!existsDomain_?()){
      createDomain()
    }else{
      false
    }
  }

  def deleteDomain() = {
    val request = new DeleteDomainRequest(domain)
    syncClient.deleteDomain(request)
    true
  }


  def put( putValues : PutRequest* ) : AWSResult[Unit,Unit] = {

    val _putValues = putValues.filter(_.values.size > 0)

    if (_putValues.size == 0){
      // nothing to do
      new AWSSuccess(Unit,Unit)
    }else if (_putValues.size == 1){
      val pv = _putValues(0)
      val request = new PutAttributesRequest(domain,
        pv.key, Implicits.toReplaceableAttributes(pv.values,pv.replace_?) )

      if (pv.exp.isDefined){
        request.setExpected(Implicits.toUpdateCondition(pv.exp.get))
      }

      try{
        syncClient.putAttributes(request)
        new AWSSuccess(Unit,Unit)
      }catch{
        case e : AmazonServiceException => {

          e.getErrorCode match{
            case SimpleDBErrorCodes.IncompleteExpectedValues => {
              new AWSError(e)
            }
            case SimpleDBErrorCodes.ConditionalCheckFailed => {
              new AWSError(e)
            }
            case _ => throw e
          }

        }
      }
    }else {

      if (putValues.exists(_.exp.isDefined)){
        if(rigidCheck){
          throw new Exception("Wrong condition.Batch update can't set UpdateCondition")
        }else{
          logger.warn("There is UpdateCondition when BatchUpdate.UpdateCondition will be ignored.")
        }
      }

      val request = new BatchPutAttributesRequest()
      new BatchPutAttributesRequest(domain,_putValues.map(
        Implicits.toReplaceableItem _
      ).toList.asJava)

      syncClient.batchPutAttributes(request)
      new AWSSuccess(Unit,Unit)
    }


  }

  def get(getInfo : GetRequest ) : AWSResult[Map[String,String],GetAttributesResult]= {

    val request = new GetAttributesRequest(domain,getInfo.key)
    if (getInfo.attributes.size > 0){
      request.setAttributeNames(getInfo.attributes.asJavaCollection)
    }
    if (getInfo.consistentRead_?){
      request.setConsistentRead(getInfo.consistentRead_?)
    }

    import Implicits._
    val result = syncClient.getAttributes(request)
    new AWSSuccess(result.getAttributes,result)


  }

  /**
   * Get only one value
   * @param getInfo
   */
  def getOne( getInfo : GetRequest) : String = {
    get(getInfo) match{
      case AWSSuccess(values,_) => {
        getInfo.attributes.find(s => values.contains(s)).get
      }
    }
  }
  def getOne( getInfo : GetRequest, default : => String) : String = {
    get(getInfo) match{
      case AWSSuccess(values,_) => {
        getInfo.attributes.find(s => values.contains(s)).getOrElse(default)
      }
      case _ => default
    }
  }

  /*def delete( columnInfos : GetRequest*) : AWSResult[Unit,Unit]  = {
    delete(columnInfos.map(columnInfo => DeleteRequest(columnInfo)) :_*)
  }*/
  def delete( columnInfos : DeleteRequest*) : AWSResult[Unit,Unit]  = {

    if (columnInfos.size == 0) AWSSuccess(Unit,Unit)
    else if (columnInfos.size == 1){
      // Single delete
      val columnInfo = columnInfos(0)
      val request = new DeleteAttributesRequest(domain,columnInfo.key)
      if (columnInfo.attributes.size > 0){
        val attributes = columnInfo.attributes.map(v => new Attribute(v,null)).asJavaCollection
        request.setAttributes(attributes)
      }
      if (columnInfo.exp.isDefined){
        request.setExpected(Implicits.toUpdateCondition(columnInfo.exp.get))
      }

      try{
        syncClient.deleteAttributes(request)
        AWSSuccess(Unit,Unit)
      }catch{
        case e : AmazonServiceException => {
          e.getErrorCode match{
            case SimpleDBErrorCodes.IncompleteExpectedValues => {
              new AWSError(e)
            }
            case SimpleDBErrorCodes.ConditionalCheckFailed => {
              new AWSError(e)
            }
            case _ => throw e
          }
        }
      }

    }else{
      // Batch delete

      if (columnInfos.exists(_.exp.isDefined)){
        if(rigidCheck){
          throw new Exception("Wrong condition.Batch delete can't set UpdateCondition")
        }else{
          logger.warn("There is UpdateCondition when BatchDelete.UpdateCondition will be ignored.")
        }
      }

      val request = new BatchDeleteAttributesRequest(domain,
        columnInfos.map( dr => {
          val item = new DeletableItem()
          item.withName(dr.key)
          if (dr.attributes.size > 0){
            item.setAttributes(dr.attributes.map(v => new Attribute(v,null)).asJavaCollection)
          }
          item
        }).asJava
      )
      syncClient.batchDeleteAttributes(request)
      AWSSuccess(Unit,Unit)
    }
  }


  def close() = {
    syncClient match{
      case c : AmazonSimpleDBClient => c.shutdown()
      case c : Closeable => c.close()
      case _ =>
    }
  }

}

object SimpleDBErrorCodes{

  val IncompleteExpectedValues = "IncompleteExpectedValues"
  val ConditionalCheckFailed = "ConditionalCheckFailed"
}