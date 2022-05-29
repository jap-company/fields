package jap.fields

import java.util.UUID
import java.time.LocalDate

case class TestData(
    boolean: Boolean = false,
    int: Int = 0,
    long: Long = 0,
    byte: Byte = 0,
    double: Double = 0,
    float: Float = 0,
    bigDecimal: BigDecimal = 0,
    bigInt: BigInt = 0,
    string: String = "",
    stringValueClass: StringValueClass = StringValueClass(""),
    mapStringString: Map[String, String] = Map.empty,
    listInt: List[Int] = Nil,
    optionInt: Option[Int] = None,
    nested: NestedTestData = NestedTestData(),
)

case class NestedTestData(
    deep: DeepTestData = DeepTestData()
)

case class DeepTestData(
    int: Int = 0
)

case class StringValueClass(value: String) extends AnyVal
