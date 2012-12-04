package com.geishatokyo.jukai

import java.util.Properties
import org.specs2.execute.{Skipped, SkipException}
import com.geishatokyo.jukai.factory.Region
import java.io.FileInputStream

/**
 * 
 * User: takeshita
 * DateTime: 12/12/04 21:52
 */
object Prop {

  val prop = {
    val p = new Properties()
    val stream = getClass.getClassLoader.getResourceAsStream("setting.properties")
    if (stream != null){
      p.load(stream)
    }
    p
  }

  def skipTest_?(name : String) = {
    _skipTest_?("all") || _skipTest_?(name)
  }

  def _skipTest_?(name : String) = {
    prop.getProperty("skip.test." + name.toLowerCase) != "false"
  }

  def checkTest(name : String) = {
    if (skipTest_?(name)){
      throw new SkipException(new Skipped("To test %s, set skip.test.%s property false".format(name,name)) )
    }
  }



  def region = Region.fromName(prop.getProperty("aws.region")).withCredentials(accessKey,secretKey)
  def accessKey = prop.getProperty("aws.accessKey")
  def secretKey = prop.getProperty("aws.secretKey")
  def s3Bucket = prop.getProperty("aws.s3.bucketname")


  def loadFile(filename : String) = {
    val f = getClass.getClassLoader.getResourceAsStream(filename)
    val d = new Array[Byte](f.available())
    f.read(d)
    f.close()

    d
  }

}
