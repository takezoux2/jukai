package com.geishatokyo.jukai.s3

import com.amazonaws.services.s3.model.{Permission, Grant, Grantee}

/**
 * 
 * User: takeshita
 * DateTime: 12/12/04 21:29
 */
case class RichGrantee(grantee : Grantee) {

  def full = permitAll
  def all = permitAll
  def permitAll = new Grant(grantee,Permission.FullControl)
  def read = permitRead
  def permitRead = new Grant(grantee,Permission.Read)
  def write = permitWrite
  def permitWrite = new Grant(grantee,Permission.Write)
  def permitReadAcp = new Grant(grantee,Permission.ReadAcp)
  def permitWriteAcp = new Grant(grantee,Permission.WriteAcp)



}
