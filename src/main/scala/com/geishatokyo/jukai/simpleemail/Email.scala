package com.geishatokyo.jukai.simpleemail

import com.amazonaws.services.simpleemail.model._
import scala.collection.JavaConverters._
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import scala.Some
import java.nio.ByteBuffer

/**
 * 
 * User: takeshita
 * DateTime: 12/12/06 13:01
 */
trait Email{


  def from(email : String) : Email

  def to(emails : String*) : Email

  def cc(emails : String*) : Email

  def bcc(emails : String*) : Email

  def replyTo(emails : String*) : Email


  def subject(s : String) : Email

  def body(s : String) : Email

  def htmlBody(s : String) : Email

  def rawBody(bytes : Array[Byte]) : Email

  /**
   * If fail to send email, error message send to this url.
   */
  def returnPath(email : String) : Email

  def send() : Unit

  def send(toEmails : String*) : Unit

}

case class ImmutableEmail(
  client : AmazonSimpleEmailService,
  from : Option[String],
  to : Seq[String],
  cc : Seq[String],
  bcc : Seq[String],
  replyTo : Seq[String],
  subject : Option[String],
  body : Option[String],
  htmlBody : Option[String],
  returnPath : Option[String]) extends Email{

  def from(email : String) : Email = copy(from=Some(email))

  def to(emails : String*) : Email = copy(to = emails)

  def cc(emails : String*) : Email = copy(cc = emails)

  def bcc(emails : String*) : Email = copy(bcc = emails)

  def replyTo(emails : String*) : Email = copy(replyTo = emails)


  def subject(s : String) : Email = copy(subject = Some(s))

  def body(s : String) : Email = copy(body = Some(s))

  def htmlBody(s : String) : Email = copy(htmlBody = Some(s))

  def rawBody(bytes : Array[Byte]) = ImmutableRawEmail(client,from,to,cc,bcc,replyTo,ByteBuffer.wrap(bytes))

  /**
   * If fail to send email, error message send to this url.
   */
  def returnPath(email : String) : Email = copy(returnPath = Some(email))

  def toRequest() : SendEmailRequest = {
    val req = new SendEmailRequest()

    from.foreach(req.setSource(_))
    if (!replyTo.isEmpty) req.setReplyToAddresses(replyTo.asJavaCollection)
    returnPath.foreach(req.setReturnPath(_))

    val dest = new Destination()
    if (!to.isEmpty) dest.setToAddresses(to.asJavaCollection)
    if (!cc.isEmpty) dest.setCcAddresses(cc.asJavaCollection)
    if (!bcc.isEmpty) dest.setBccAddresses(bcc.asJavaCollection)
    req.setDestination(dest)

    val message = new Message()
    subject.foreach(s => {
      val c = new Content()
      c.setData(s)
      message.setSubject(c)
    })
    val mailBody = new Body()
    body.foreach(v => mailBody.setText(new Content(v)))
    htmlBody.foreach(v => mailBody.setHtml(new Content(v)))
    message.setBody(mailBody)
    req.setMessage(message)

    req
  }

  def send() {
    client.sendEmail(toRequest())
  }

  def send(toEmails: String*) {
    this.to(toEmails:_*).send()
  }
}

object ImmutableEmail{

  def apply(client : AmazonSimpleEmailService) : ImmutableEmail = {
    ImmutableEmail(client,None,Seq(),Seq(),Seq(),Seq(),None,None,None,None)
  }

}

case class ImmutableRawEmail(
                           client : AmazonSimpleEmailService,
                           from : Option[String],
                           to : Seq[String],
                           cc : Seq[String],
                           bcc : Seq[String],
                           replyTo : Seq[String],
                           rawBody : ByteBuffer) extends Email{

  def from(email : String) : Email = copy(from=Some(email))

  def to(emails : String*) : Email = copy(to = emails)

  def cc(emails : String*) : Email = copy(cc = emails)

  def bcc(emails : String*) : Email = copy(bcc = emails)

  def replyTo(emails : String*) : Email = throw new Exception("Not supported")


  def subject(s : String) : Email = ImmutableEmail(client,from,to,cc,bcc,replyTo,Some(s),None,None,None)

  def body(s : String) : Email = ImmutableEmail(client,from,to,cc,bcc,replyTo,None,Some(s),None,None)

  def htmlBody(s : String) : Email = ImmutableEmail(client,from,to,cc,bcc,replyTo,None,None,Some(s),None)

  def rawBody(bytes : Array[Byte]) = copy(rawBody = ByteBuffer.wrap(bytes))

  /**
   * If fail to send email, error message send to this url.
   */
  def returnPath(email : String) : Email = throw new Exception("Not supported")

  def toRequest() : SendRawEmailRequest = {
    val req = new SendRawEmailRequest()

    from.foreach(req.setSource(_))

    req.setDestinations( (to :: cc :: bcc :: Nil).flatten.asJavaCollection)

    val message = new RawMessage(rawBody)
    req.setRawMessage(message)

    req
  }

  def send() {
    client.sendRawEmail(toRequest())
  }

  def send(toEmails: String*) {
    this.to(toEmails:_*).send()
  }
}