package com.geishatokyo.jukai.util

import org.specs2.mutable.SpecificationWithJUnit

/**
 * 
 * User: takeshita
 * DateTime: 12/12/04 22:28
 */
class StringUtilTest extends SpecificationWithJUnit {

  "Camel case to hyphen" should{

    "convert" in{

      StringUtil.camelToHyphen("LittleBusters") must_== "little-busters"

    }

  }

}
