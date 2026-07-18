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
import scala.reflect.macros.blackbox

class FieldPathMacro(val c: blackbox.Context) {
  import c.universe.*

  def pathPart(path: String): Tree = q"fields.FieldPart.Path($path)"
  def keyPart(path: String): Tree  = q"fields.FieldPart.Key($path)"
  def indexPart(index: Int): Tree  = q"fields.FieldPart.Index($index)"

  def selectorPath(tree: Tree, includeIdent: Boolean, title: String): List[Tree] = {
    @tailrec
    def go(tree: Tree, acc: List[Tree] = Nil): List[Tree] =
      tree match {
        case Function(_, body)               => go(body, acc)
        case Typed(rest, _)                  => go(rest, acc)
        case Ident(name: TermName)           => if (includeIdent) pathPart(name.toString) :: acc else acc
        case Select(This(_), name: TermName) => if (includeIdent) pathPart(name.toString) :: acc else acc
        case Select(rest, name: TermName)    => go(rest, pathPart(name.toString) :: acc)
        case Apply(Select(rest, TermName("apply")), List(Literal(Constant(path: String)))) =>
          go(rest, keyPart(path) :: acc)
        case Apply(Select(rest, TermName("apply")), List(Literal(Constant(index: Int))))   =>
          go(rest, indexPart(index) :: acc)
        case _ => c.abort(c.enclosingPosition, FieldMacroMessage.selectorErrorMessage(title))
      }

    go(tree)
  }

  def subMacro[A](value: c.Expr[A]): c.Expr[FieldPath] = {
    // c.info(c.enclosingPosition, showRaw(value.tree), true)
    val parts = selectorPath(value.tree, includeIdent = false, title = "Field.sub")
    c.Expr[FieldPath](q"""fields.FieldPath($parts)""")
  }

  def fromMacro[A](value: c.Expr[A]): c.Expr[FieldPath] = {
    // c.info(c.enclosingPosition, showRaw(value.tree), true)
    val parts = selectorPath(value.tree, includeIdent = true, title = "Field.from")
    c.Expr[FieldPath](q"""fields.FieldPath($parts)""")
  }
}
