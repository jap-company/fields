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

import scala.annotation.{nowarn, tailrec}
import scala.reflect.macros.blackbox

class FieldMacro(val c: blackbox.Context) {
  import c.universe._

  def selectorPath(tree: Tree, includeIdent: Boolean, title: String): List[String] = {
    @tailrec
    def go(tree: Tree, acc: List[String] = Nil): List[String] =
      tree match {
        case Ident(name: TermName)           => if (includeIdent) name.toString :: acc else acc
        case Select(This(_), name: TermName) => if (includeIdent) name.toString :: acc else acc
        case Select(rest, name: TermName)    => go(rest, name.toString :: acc)
        case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => go(rest, name.toString :: acc)
        case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => go(rest, name.toString :: acc)
        case _ => c.abort(c.enclosingPosition, FieldMacroMessage.selectorErrorMessage(title))
      }

    go(tree)
  }

  def fromSubMacro[A](value: c.Expr[A]): c.Expr[Field[A]] = {
    // c.info(c.enclosingPosition, showRaw(value.tree), true)
    val parts = selectorPath(value.tree, includeIdent = false, title = "Field.sub")
    val path  = q"jap.fields.FieldPath(${parts.map(n => Literal(Constant(n)))})"
    c.Expr[Field[A]](q"""jap.fields.Field($path, $value)""")
  }

  def fromMacro[A](value: c.Expr[A]): c.Expr[Field[A]] = {
    // c.info(c.enclosingPosition, showRaw(value.tree), true)
    val parts = selectorPath(value.tree, includeIdent = true, title = "Field.from")
    val path  = q"jap.fields.FieldPath(${parts.map(n => Literal(Constant(n)))})"
    c.Expr[Field[A]](q"""jap.fields.Field($path, $value)""")
  }

  @nowarn
  def subMacro[P, S](selector: c.Expr[P => S]): c.Expr[Field[S]] = {
    import c.universe._
    val q"($_) => $body"        = selector.tree
    val q"$_.$_[..$_]($parent)" = c.prefix.tree
    // c.info(c.enclosingPosition, showRaw(body), true)
    val parts                   = selectorPath(body, includeIdent = false, title = "Field#sub")
    val path                    = q"jap.fields.FieldPath(${parts.map(n => Literal(Constant(n)))})"
    c.Expr[Field[S]](q"""jap.fields.Field($parent.path ++ $path, $selector($parent.value))""")
  }

  @nowarn
  def policySubRuleMacro[P, S, F[_], V[_], E](selector: c.Expr[P => S])(rules: c.Tree*): c.Tree = {
    import c.universe._
    val q"$_.$_[..$_]($builder)" = c.prefix.tree
    q"$builder.fieldRule(_.sub($selector))(..$rules)"
  }

  @nowarn
  def policySubRule2Macro[P, S1, S2, F[_], V[_], E](selector1: c.Expr[P => S1], selector2: c.Expr[P => S2])(
      rules: c.Tree*
  ): c.Tree = {
    import c.universe._
    val q"$_.$_[..$_]($builder)" = c.prefix.tree
    q"$builder.fieldRule(_.sub($selector1), _.sub($selector2))(..$rules)"
  }

  @nowarn
  def policySubRule3Macro[P, S1, S2, S3, F[_], V[_], E](
      selector1: c.Expr[P => S1],
      selector2: c.Expr[P => S2],
      selector3: c.Expr[P => S3],
  )(rules: c.Tree*): c.Tree = {
    import c.universe._
    val q"$_.$_[..$_]($builder)" = c.prefix.tree
    q"$builder.fieldRule(_.sub($selector1), _.sub($selector2), _.sub($selector3))(..$rules)"
  }
}
