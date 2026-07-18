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

import scala.annotation.tailrec
import scala.quoted.*
import FieldPart.*

class SelectorMacro[Q <: Quotes](using val q: Q) {
  import q.reflect._

  given FieldPartToExpr: ToExpr[FieldPart] with {
    def apply(part: FieldPart)(using Quotes) =
      part match {
        case Key(value)   => '{ Key(${ Expr(value) }) }
        case Path(value)  => '{ Path(${ Expr(value) }) }
        case Index(value) => '{ Index(${ Expr(value) }) }
      }
  }

  // Recursively extracts names from call chain
  def selectorPath(term: Term, includeIdent: Boolean, title: String): Expr[List[FieldPart]] = {
    @tailrec
    def go(term: Term, acc: List[FieldPart] = Nil): List[FieldPart] =
      term match {
        case Typed(term, _)                              => go(term, acc)
        case Ident(name)                                 => if (includeIdent) Path(name) :: acc else acc
        case Select(This(_), name)                       => if (includeIdent) Path(name) :: acc else acc
        case Select(rest, name)                          => go(rest, Path(name) :: acc)
        case Block(List(DefDef(_, _, _, Some(term))), _) => go(term, acc)
        case Block(_, term)                              => go(term, acc)
        case Inlined(_, _, rest)                         => go(rest, acc)
        case Apply(Select(rest, "apply"), List(Literal(IntConstant(index))))  => go(rest, Index(index) :: acc)
        case Apply(Select(rest, "apply"), List(Literal(StringConstant(key)))) => go(rest, Key(key) :: acc)
        case _ => report.errorAndAbort(FieldMacroMessage.selectorErrorMessage(title))
//          report.errorAndAbort(unmatched.show(using Printer.TreeStructure))
      }

    Expr(go(term))
  }
}

object FieldPathMacro {
  def fromImpl[P: Type](value: Expr[P], includeIdent: Boolean, title: String)(using q: Quotes) = {
    import quotes.reflect._
    // report.info(value.asTerm.show(using Printer.TreeStructure))
    val parts = new SelectorMacro[q.type].selectorPath(value.asTerm, includeIdent, title)
    '{ FieldPath(${ parts }) }
  }
}
