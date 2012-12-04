package com.geishatokyo.jukai

import org.specs2.mutable.Specification
import org.specs2.execute.AsResult

/**
 * 
 * User: takeshita
 * DateTime: 12/12/04 22:59
 */
trait AWSTesting {

  self : Specification =>

  def runIfEnabled[T](name : String)( func : => T) : T = {

    if(Prop.skipTest_?(name)){
      skipped("To test %s, please enable skip.test.%s false".format(name,name.toLowerCase))
    }else{
      func
    }


  }


}
