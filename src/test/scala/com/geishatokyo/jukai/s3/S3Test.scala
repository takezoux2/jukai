package com.geishatokyo.jukai.s3

import org.specs2.mutable.SpecificationWithJUnit
import com.geishatokyo.jukai.{Prop, AWSTesting}
import S3Implicits._
import com.amazonaws.services.s3.model.ObjectListing

/**
 * 
 * User: takeshita
 * DateTime: 12/12/05 0:02
 */
class S3Test extends SpecificationWithJUnit with AWSTesting{

  def withBucket[T]( func : S3 => T) = {
    runIfEnabled("s3"){
      val s3 = Prop.region.s3

      val bucket = s3.bucket(Prop.s3Bucket)
      bucket.createBucketIfNotExists()

      func(bucket)

      s3.shutdown()

    }
  }


  "write" should{
    "write image" in withBucket(bucket => {
      val data = Prop.loadFile("sample.png")
      bucket += ("kudo.png" -> data)

      bucket("kudo.png").length must_== data.length

    })

    "overwrite image" in withBucket(bucket => {
      val data = Prop.loadFile("sample.png")
      bucket += ("overwrite.png" -> data)
      Thread.sleep(500)
      bucket += ("overwrite.png" -> data)

      bucket("overwrite.png").length must_== data.length

    })
  }

  "list up" should{


    "list up" in withBucket(bucket => {

      bucket +=("a" -> "a".getBytes)
      bucket +=("b" -> "a".getBytes)
      bucket +=("hoge/c" -> "a".getBytes)
      bucket +=("hoge/d" -> "a".getBytes)
      bucket +=("hoge/fuga/e" -> "a".getBytes)
      bucket +=("hoge/fuga/wahoo/f" -> "a".getBytes)
      bucket +=("hogeg" -> "a".getBytes)
      bucket +=("hogehoge/h" -> "a".getBytes)
      Thread.sleep(500)

      {
        val data = bucket.listObjects()
        println("1#" + data.toKeys.toList)
      }


      {
        val data = bucket.listObjects("hoge").toKeys.toList
        println("2#" + data)
        data.size must_== 6
      }

      {
        val data = bucket.listObjects("hoge",2).toKeys.toList
        println("3#" + data)
        data.size must_== 2
        data must_== List("hoge/c","hoge/d")
      }

      {
        val (_data,others) = bucket.listObjects(prefix("hoge") delimitedBy("/"))
        val data = _data.toKeys.toList
        println("4#" + data + "," + others)
        data must_== List("hogeg")
        others must_== List("hoge/","hogehoge/")
      }

      ok
    })
  }

  "get" should{
    "get not exist data without exception" in withBucket(bucket => {
      bucket.get("key.not.exists") must beNone
    })
  }

  "delete" should{
    "delete not exist data without exception" in withBucket(bucket => {
      bucket -> ("key.not.exists")

      ok
    })
  }


}
