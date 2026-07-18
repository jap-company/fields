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

package fields
package lens.syntax

object GetByIndexMacro {
  import scala.quoted.*

  inline def getterByIndex[T, S](inline index: Int): T => S =
    ${ getterByIndexImpl[T, S]('index) }

  private def getterByIndexImpl[T: Type, S: Type](indexExpr: Expr[Int])(using Quotes): Expr[T => S] =
    import quotes.reflect.*

    indexExpr.value match
      case Some(idx) =>
        val tpe    = TypeRepr.of[T]
        val fields = tpe.typeSymbol.caseFields

        if idx < 0 || idx >= fields.size then report.errorAndAbort(s"Invalid index $idx for case class ${tpe.show}")

        Lambda(
          owner = Symbol.spliceOwner,
          tpe = MethodType(List("t"))(_ => List(tpe), _ => TypeRepr.of[S]),
          rhsFn = (_, params) =>
            val tRef = params.head.asInstanceOf[Term]
            Select(tRef, fields(idx)).asExprOf[Any].asTerm,
        ).asExprOf[T => S]

      case None =>
        report.errorAndAbort("Index must be a known constant at compile time")

}
