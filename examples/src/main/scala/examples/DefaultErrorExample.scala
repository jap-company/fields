package jap.fields
package examples

import ValidationResult.Accumulate._
import Common._
import DefaultAccumulateValidationModule._

case class Ann(
    age: Int,
    clothes: Clothes,
)

case class Clothes(
    backpack: Backpack
)

case class Backpack(
    smallPocket: Pocket,
    bigPocket: Pocket,
)

case class Pocket(
    items: List[String]
)

case class Home(ann: Ann)

object DefaultErrorExampleApp {
  final def main(args: Array[String]) = {
    val ann  = Ann(
      age = 22,
      clothes = Clothes(
        Backpack(
          bigPocket = Pocket(List("1", "2", "", "4")),
          smallPocket = Pocket(List()),
        )
      ),
    )
    val home = Home(ann)

    val clothesF        = Field.from(home.ann).sub(_.clothes)
    val backpackF       = clothesF.sub(_.backpack)
    val bigPocketF      = backpackF.sub(_.bigPocket)
    val bigPocketDeepF  = clothesF.sub(_.backpack.bigPocket)
    val bigPocketItemsF = clothesF.sub(_.backpack.bigPocket.items)

    println(bigPocketItemsF.min(1).and(bigPocketItemsF.max(3)))

    implicit val clothesPolicy: Policy[Clothes] =
      Policy
        .builder[Clothes]
        .rule { clothesF =>
          val backpackF = clothesF.sub(_.backpack)

          backpackF.sub(_.bigPocket.items).nonEmpty and
            backpackF.sub(_.smallPocket.items).nonEmpty
        }
        .build

    implicit val listStringPolicy: Policy[List[String]] =
      Policy
        .builder[List[String]]
        .rule(_.min(1))
        .rule(_.max(3))
        .rule(_.assert(_.exists(_ == "24"), _.custom("24")))
        .rule(_.each(_.nonEmpty))
        .build

    println(bigPocketItemsF.validate)
    println(clothesF.validate)
    println(backpackF)
    println(bigPocketF)
    println(bigPocketDeepF)
  }
}
