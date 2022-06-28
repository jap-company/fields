package jap.fields
package examples
package tagless

import cats.Eval
import jap.fields._
import jap.fields.fail._
import jap.fields.typeclass._

// You like magic codes?)
case class ERR(code: Int)

trait ErrFailWithInstance {
  implicit object FailWithErr extends FailWith[ERR, Nothing] {
    def message[P](error: String, message: Option[String])(field: Field[P]): ERR        = ERR(error.toInt)
    def invalid[P](field: Field[P]): ERR                                                = ERR(2)
    def empty[P](field: Field[P]): ERR                                                  = ERR(3)
    def oneOf[P](variants: Seq[P])(field: Field[P]): ERR                                = ERR(4)
    def minSize[P](size: Int)(field: Field[P]): ERR                                     = ERR(5)
    def compare[P](operation: CompareOperation, compared: String)(field: Field[P]): ERR = ERR(6)
    def nonEmpty[P](field: Field[P]): ERR                                               = ERR(7)
    def maxSize[P](size: Int)(field: Field[P]): ERR                                     = ERR(8)
  }
}

object Validation {
  import jap.fields.CatsInterop._
  object all extends AccumulateVM[Eval, ERR] with ErrFailWithInstance
}

import Validation.all._

object FailWithOverrideExample {
  showBuildInfo()
  implicit object FailWithEmptyString extends FailWithCompare[ERR, String] {
    override def compare[P >: String](operation: CompareOperation, compared: String)(field: Field[P]): ERR =
      ERR(44)
  }

  implicit final def main(args: Array[String]): Unit = {
    val intF    = Field(1)
    val stringF = Field("asd")

    println("intF.equalTo - " + intF.equalTo(2).errors.value)
    println("intF.gt - " + intF.gt(2).errors.value)
    println("stringF.equalTo - " + stringF.equalTo("").errors.value)
    println("stringF.gt - " + stringF.gt("b").errors.value)
  }
}
