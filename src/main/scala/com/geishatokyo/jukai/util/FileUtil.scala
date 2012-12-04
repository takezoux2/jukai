package com.geishatokyo.jukai.util

/**
 * Created with IntelliJ IDEA.
 * User: takezoux3
 * Date: 12/12/04
 * Time: 19:11
 * To change this template use File | Settings | File Templates.
 */
object FileUtil {


  def getExtention(s : String) : String = {
    val i = s.lastIndexOf(".")
    if (i >= 0){
      s.substring(i + 1)
    }else{
      ""
    }
  }


}
