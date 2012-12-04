package com.geishatokyo.jukai

import com.amazonaws.auth.{BasicAWSCredentials, AWSCredentials, AWSCredentialsProvider}

/**
 * 
 * User: takeshita
 * DateTime: 12/11/01 23:40
 */
case class SimpleCredentialsProvider(c : AWSCredentials) extends AWSCredentialsProvider {
  def getCredentials = {
    c
  }

  def refresh() {}
}

object SimpleCredentialsProvider{

  def apply(accessKey : String, secretKey : String) : SimpleCredentialsProvider = {
    new SimpleCredentialsProvider(new BasicAWSCredentials(accessKey,secretKey))
  }

}
