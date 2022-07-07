/*
 * Copyright 2022 Jap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jap.fields

import jap.fields.syntax.all._
import jap.fields.typeclass._

import scala.concurrent._
import scala.concurrent.duration._
import java.time.LocalDateTime

package object examples {
  implicit val localDateTimeOrdering: Ordering[LocalDateTime] = _ compareTo _

  def showTitle(name: String, size: Int = 40, symbol: String = "-") = {
    val toFill = (size - name.size) / 2.0
    val before = symbol * Math.ceil(toFill).toInt
    val after  = symbol * Math.floor(toFill).toInt
    println(before + name + after)
  }

  def showErrors[F[_]: Effect, V[_]: HasErrors, E](title: String)(rule: Rule[F, V, E]): F[Unit] =
    Effect[F].map(rule.errors) { errors =>
      showTitle(title)
      println(errors.mkString("\n"))
      ()
    }

  def awaitResult[T](f: Future[T]): T   = Await.result(f, Duration.Inf)
  def awaitReady[T](f: Future[T]): Unit = {
    Await.ready(f, Duration.Inf)
    ()
  }

  def showBuildInfo() = {
    showTitle("BUILD-INFO")
    println(jap.fields.BuildInfo)
    showTitle("")
  }
}
