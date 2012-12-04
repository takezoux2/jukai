package com.geishatokyo.jukai.s3

import com.amazonaws.services.s3.model._

/**
 * 
 * User: takeshita
 * DateTime: 12/12/04 21:28
 */
object S3Implicits {


  implicit def toRichGrantee(grantee : Grantee) = new RichGrantee(grantee)

  implicit def toAccessControlList(grant : Grant) = {
    val al = new AccessControlList()
    al.grantAllPermissions(grant)
    al
  }

  def allUses = GroupGrantee.AllUsers
  def authenticatedUsers = GroupGrantee.AuthenticatedUsers
  def logDelivery = GroupGrantee.LogDelivery

  def email(email : String) = new EmailAddressGrantee(email)

  def amazonId(amazonId : String) = new CanonicalGrantee(amazonId)
  def canonicalId(amazonId : String) = this.amazonId(amazonId)


}
