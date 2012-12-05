package com.geishatokyo.jukai.util

import java.io.{ByteArrayOutputStream, InputStream}

/**
 * Created with IntelliJ IDEA.
 * User: takezoux3
 * Date: 12/12/04
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
object IOUtil {

  val DefaultBufferSize = 1000000;

  def readStream(inputStream : InputStream, bufferSize : Int = DefaultBufferSize) : Array[Byte] = {
    val buffer = new Array[Byte](bufferSize)
    val bao = new ByteArrayOutputStream(bufferSize)
    var n = 1
    while(n > 0){
      n = inputStream.read(buffer)
      if(n > 0){
        bao.write(buffer,0,n)
      }
    }
    bao.toByteArray
  }

  def closeQuietly(stream : InputStream) = {
    try{
      stream.close()
    }catch{
      case e : Throwable => // ignore
    }
  }



}
