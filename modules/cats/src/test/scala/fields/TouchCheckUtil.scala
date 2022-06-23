package jap.fields

import scala.collection.mutable.ListBuffer

final case class TouchCheckUtil[K]() {
  private val touchedBuffer      = new ListBuffer[K]
  def apply[A](key: K)(a: A): A  = { touchedBuffer.append(key); a }
  def touched: List[K]           = touchedBuffer.toList
  def isTouched(key: K): Boolean = touchedBuffer.contains(key)
  def clear(): Unit              = touchedBuffer.clear()
}
