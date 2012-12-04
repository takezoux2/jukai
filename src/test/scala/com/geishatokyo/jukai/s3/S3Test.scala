package com.geishatokyo.jukai.s3

import org.specs2.mutable.SpecificationWithJUnit
import com.geishatokyo.jukai.{Prop, AWSTesting}

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
  }


}
