package com.geishatokyo.jukai.util

/**
 * 
 * User: takeshita
 * DateTime: 12/12/04 22:23
 */
object StringUtil {

  def camelToHyphen(str : String) = {
    camelToDelimit(str,"-")
  }

  def camelToSnake(str : String) = {
    camelToDelimit(str,"_")
  }

  def camelToDelimit( str : String, delimit : String) = {

    str.flatMap( c => {
      if (c.isUpper){
        delimit + c.toLower
      }else{
        c.toString
      }
    }).stripPrefix(delimit)


  }


}
