package jap.fields

import jap.fields.syntax.all._
import jap.fields.typeclass._

import scala.concurrent._
import scala.concurrent.duration._

package object examples {
  def showTitle(name: String, size: Int = 40, symbol: String = "-") = {
    val toFill = (size - name.size) / 2.0
    val before = symbol * Math.ceil(toFill).toInt
    val after  = symbol * Math.floor(toFill).toInt
    println(before + name + after)
  }

  def showErrors[F[_]: Effect, V[_]: Validated, E](title: String)(rule: Rule[F, V, E]): F[List[E]] =
    Effect[F].map(rule.errors) { errors =>
      showTitle(title)
      println(errors.mkString("\n"))
      errors
    }

  def awaitFuture[T](f: Future[T]): T = Await.result(f, Duration.Inf)

  def showBuildInfo() = {
    showTitle("BUILD-INFO")
    println(jap.fields.BuildInfo)
    showTitle("")
  }
}
