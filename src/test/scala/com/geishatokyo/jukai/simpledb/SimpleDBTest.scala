package com.geishatokyo.jukai.simpledb

import org.specs2.mutable.{After, SpecificationWithJUnit}
import com.geishatokyo.jukai.{AWSTesting, Prop}
import SimpleDBImplicits._

/**
 * 
 * User: takeshita
 * DateTime: 12/12/04 21:51
 */
class SimpleDBTest extends SpecificationWithJUnit with AWSTesting {

  def withDomain[T](domainName : String)( func : (SimpleDB) => T) : T = runIfEnabled("simpleDb") {
    val db = Prop.region.simpleDb

    val domain = db.domain(domainName)
    domain.createDomainIfNotExists()
    try{
      func(domain)
    }finally{
      domain.deleteDomain()
      db.shutdown()
    }
  }



  "write" should{

    "write simply" in withDomain("jukai-test-write")( db => {

      db.put("LittleBusters" -> "Natsume" -> "Rin")
      db.put("Air" -> Map(
        "Kamio" -> "Misuzu",
        "Tono"  -> "Minagi"
        ))
      db.put("Clanad")("Okazaki" -> "Nagisa","Fujibayashi" -> "Kyo","Ibuki" -> "Fuko")

      db.getOne("LittleBusters" -> "Natsume" consistently) must_== "Rin"

      {
        val d = db.get("Air" consistently).result
        d("Kamio") must_== "Misuzu"
        d("Tono") must_== "Minagi"
      }
      {
        val d = db.get("Clanad" -> Seq("Fujibayashi","Ibuki") consistently).result
        d.size must_== 2
        d("Fujibayashi") must_== "Kyo"
        d("Ibuki") must_== "Fuko"
      }

    })

    "write with condition" in withDomain("jukai-test-write2")( db => {
      db.put("Rewrite")("Nakatsu" -> "Shizuru")

      Thread.sleep(500)

      db.put("Rewrite" -> Map("Konohana" -> "Ruchia") when("Nakatsu" === "Shizuru")) // OK
      db.put("Rewrite" -> Map("Tenouji" -> "Kotarou") when("Nakatsu" === "Kotarou")) // NG
      db.put("Rewrite" -> Map("Kanbe" -> "Kotori") when("Otori" exist))           // NG

      val d = db.get("Rewrite" consistently).result

      d must_== Map("Nakatsu" -> "Shizuru","Konohana" -> "Ruchia")

    })

  }


}
