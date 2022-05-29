package jap.fields

import java.util.UUID
import java.time.LocalDate

case class TestData(
    int: Int = 0,
    double: Double = 0,
    float: Float = 0,
    long: Long = 0,
    bigDecimal: BigDecimal = 0,
    bigInt: BigInt = 0,
    string: String = "",
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
