package com.geishatokyo.jukai.simpleemail

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService

/**
 * 
 * User: takeshita
 * DateTime: 12/12/06 12:37
 */
class SimpleEmail( val client : AmazonSimpleEmailService) {

  def email : Email = ImmutableEmail(client)


  def reactivate(email : Email) = {
    email match{
      case e : ImmutableEmail => e.copy(client = client)
      case e : ImmutableRawEmail => e.copy(client = client)
    }
  }

  def shutdown() = {
    client.shutdown()
  }

}
