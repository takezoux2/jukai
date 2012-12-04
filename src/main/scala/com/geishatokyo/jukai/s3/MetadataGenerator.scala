package com.geishatokyo.jukai.s3

import com.amazonaws.services.s3.model.ObjectMetadata
import com.geishatokyo.jukai.util.FileUtil

/**
 * Created with IntelliJ IDEA.
 * User: takezoux3
 * Date: 12/12/04
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
trait MetadataGenerator {

  def generateMetadata(key : String, data : Array[Byte]) : ObjectMetadata
}

object DefaultMetadataGenerator extends MetadataGenerator{
  val extensionMaps = Map(
    "jpg"  -> "image/jpeg",
    "jpeg" -> "image/jpeg",
    "gif"  -> "image/gif",
    "png"  -> "image/png",
    "bmp"  -> "image/bmp",
    "svg"  -> "image/svg+xml",
    "mp3"  -> "audio/mpeg",
    "ogg"  -> "audio/ogg",
    "mp4"  -> "audio/mp4" ,
    "aac"  -> "audio/mp4",
    "m4a"  -> "audio/mp4",
    "wav"  -> "audio/x-wav",
    "txt"  -> "text/plain",
    "html" -> "text/html",
    "htm"  -> "text/html",
    "json" -> "text/json",
    "css"  -> "text/css",
    "csv"  -> "text/csv",
    "js"   -> "text/javascript",
    "mpeg" -> "video/mpeg",
    "mpg"  -> "video/mpeg",
    "avi"  -> "video/mpeg",
    "wmv"  -> "video/x-ms-wmv",
    "swf"  -> "appliction/x-shockwave-flash",
    "doc"  -> "appliction/msword",
    "xls"  -> "appliction/vnd.ms-excel",
    "ppt"  -> "appliction/vnd.ms-powerpoint",
    "ppt"  -> "appliction/vnd.ms-powerpoint",
    "pdf"  -> "application/pdf",
    "zip"  -> "appliction/zip",
    "lzh"  -> "appliction/x-lzh",
    "lha"  -> "appliction/x-lzh",
    "tar"  -> "appliction/x-tar",
    "tgz"  -> "appliction/x-tar",
    "rar"  -> "appliction/x-rar-compressed"
  )

  val DefaultContentType = "application/octet-stream"

  def generateMetadata(key: String, data: Array[Byte]): ObjectMetadata = {
    val mt = new ObjectMetadata()
    val ctOp = extensionMaps.get(FileUtil.getExtention(key))
    if (ctOp.isDefined){
      mt.setContentType(ctOp.get)
    }else{
      mt.setContentType(DefaultContentType)
    }
    mt.setContentLength(data.length)
    mt
  }
}
