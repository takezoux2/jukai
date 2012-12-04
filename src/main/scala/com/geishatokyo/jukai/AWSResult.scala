package com.geishatokyo.jukai

/**
 *
 * User: takeshita
 * DateTime: 12/11/01 22:40
 */
trait AWSResult[ResultType,ResponseType] {

  def success : Boolean

  def resultOp : Option[ResultType]
  def result = resultOp.get

}

case class AWSSuccess[R,T](override val result : R,val response : T) extends AWSResult[R,T]{
  def success = true
  def resultOp = Some(result)
}



case class AWSFail[R,T](response : T) extends AWSResult[R,T]{
  def success = false
  def resultOp = None
}
case class AWSError[R,T](exception : Exception) extends AWSResult[R,T]{
  def success = false
  def resultOp = None
}

trait ConsistencyError
