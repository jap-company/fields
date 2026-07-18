package benchmark

import octopus.ValidationResult
import octopus.dsl.*
import octopus.syntax.*

object OctopusPolicy {
  implicit lazy val syncValidator: Validator[BenchmarkData]             = {
    implicit val deepValidator: Validator[DeepBenchmarkData] = Validator[DeepBenchmarkData]
      .rule[Boolean](_.boolean, b => b, "must be true")
      .rule[Int](_.int, _ >= 1, "must be greater than 0")
      .rule[Int](_.int, _ <= 10, "must be less then equal than 10")
      .rule[Long](_.long, _ > 0L, "must be greater than 0")
      .rule[Byte](_.byte, _ >= 0.toByte, "must be greater than or equal to 0")
      .rule[Double](_.three, _ < 0d, "must be less than 0")
      .rule[Float](_.float, _ <= -1f, "must be less than or equal to -1")
      .rule[BigDecimal](_.bigDecimal, _ == BigDecimal(1), "must be equal to BigDecimal(1)")
      .rule[BigInt](_.bigInt, _ != BigInt(0), "must not be equal to BigInt(0)")
      .rule[String](_.string, _.nonEmpty, "must not be empty")
      .rule[String](_.string, _.length >= 4, "must have a size bigger than 4")
      .rule[String](_.string, _.length <= 10, "must have size less than 10")
      .rule[List[Int]](_.listInt, _.forall(_ >= 0), "each element must be less than 0")
      .rule[Option[Int]](_.optionInt, _.forall(_ >= 0), "must be greater than 0")

    Validator.derived[BenchmarkData]
  }

  def validateSync(data: BenchmarkData): ValidationResult[BenchmarkData]          = data.validate
}
