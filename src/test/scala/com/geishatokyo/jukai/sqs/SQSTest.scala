package com.geishatokyo.jukai.sqs

import org.specs2.mutable.SpecificationWithJUnit
import com.geishatokyo.jukai.{Prop, AWSTesting}
import SQSImplicits._

/**
 * Created with IntelliJ IDEA.
 * User: takezoux3
 * Date: 12/12/05
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */
class SQSTest extends SpecificationWithJUnit with AWSTesting {

  def withQueue[T](queueName : String)( func : SQS => T) : T = {
    runIfEnabled("sqs"){
      val sqsConnection = Prop.region.sqs
      val queueUrl = sqsConnection.createQueue(queueName)
      val queue = sqsConnection.queueFromUrl(queueUrl)

      try{
        func(queue)
      }finally{
        //sqsConnection.deleteQueueByName(queueName)
        sqsConnection.shutdown()
      }
    }
  }

  "queue" should{
    "queue and dequeue" in withQueue("jukai-test-sqs1")( sqs => {
      sqs.sendMessage("Test message")

      Thread.sleep(500)

      val m = sqs.receiveAMessage()

      m.getBody must_== "Test message"

      sqs.deleteMessage(m)

    })


    "dequeu timeout as such" in withQueue("jukai-test-sqs2")( sqs => {
      val m = sqs.receiveMessage(1)

      m must haveSize(0)

    })

    "dequeu with auto delete" in withQueue("jukai-test-sqs3")( sqs => {

      sqs.sendMessage("Message1")
      sqs.sendMessage("Message2")

      Thread.sleep(3000)

      val messages = sqs.processMessage(byDefault, m => {
        m.getBody
      })

      println("$$ " + messages)

      messages must haveSize(2)

    })
  }

}
