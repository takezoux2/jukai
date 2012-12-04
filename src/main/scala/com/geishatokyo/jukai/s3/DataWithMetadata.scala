package com.geishatokyo.jukai.s3

import com.amazonaws.services.s3.model.ObjectMetadata

/**
 * Created with IntelliJ IDEA.
 * User: takezoux3
 * Date: 12/12/04
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
case class DataWithMetadata(data : Array[Byte],metadata : ObjectMetadata) {

  def withContentType( contentType : String) : DataWithMetadata = {
    this
  }
}
