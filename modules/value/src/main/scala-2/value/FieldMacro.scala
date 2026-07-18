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
package value

import scala.reflect.macros.blackbox

class FieldMacro(val c: blackbox.Context) {
  import c.universe.*

  def fromSubMacro[A](value: c.Expr[A]): c.Expr[Field[A]] = {
    c.Expr[Field[A]](q"""fields.value.Field(fields.FieldPath.sub($value), $value)""")
  }

  def fromMacro[A](value: c.Expr[A]): c.Expr[Field[A]] = {
    c.Expr[Field[A]](q"""fields.value.Field(fields.FieldPath.from($value), $value)""")
  }

  def subMacro[P, S](selector: c.Expr[P => S]): c.Expr[Field[S]] = {
    import c.universe.*
    val body   = selector.tree match {
      case Function(_, value) => value
      case _                  => c.abort(c.enclosingPosition, "Expected a function selector")
    }
    val parent = c.prefix.tree match {
      case Apply(_, List(value)) => value
      case _                     => c.abort(c.enclosingPosition, "Expected a field")
    }
    // c.info(c.enclosingPosition, showRaw(body), true)
    c.Expr[Field[S]](
      q"""fields.value.Field($parent.path ++ fields.FieldPath.sub($body), $selector($parent.value))"""
    )
  }

  def policySubRuleMacro[P, S, F[_], V[_], E](selector: c.Expr[P => S])(rules: c.Tree*): c.Tree = {
    import c.universe.*
    val builder = c.prefix.tree match {
      case Apply(_, List(value)) => value
      case _                     => c.abort(c.enclosingPosition, "Expected a policy builder")
    }
    q"""val sp = fields.FieldPath.sub($selector)
        $builder.mappedRule(_.downS(sp, $selector))(..$rules)"""
  }

  def policySubRule2Macro[P, S1, S2, F[_], V[_], E](selector1: c.Expr[P => S1], selector2: c.Expr[P => S2])(
      rules: c.Tree*
  ): c.Tree = {
    import c.universe.*
    val builder = c.prefix.tree match {
      case Apply(_, List(value)) => value
      case _                     => c.abort(c.enclosingPosition, "Expected a policy builder")
    }
    q"""val sp1 = fields.FieldPath.sub($selector1)
        val sp2 = fields.FieldPath.sub($selector2)
        $builder.mappedRule(_.downS(sp1, $selector1), _.downS(sp2, $selector2))(..$rules)"""
  }

  def policySubRule3Macro[P, S1, S2, S3, F[_], V[_], E](
      selector1: c.Expr[P => S1],
      selector2: c.Expr[P => S2],
      selector3: c.Expr[P => S3],
  )(rules: c.Tree*): c.Tree = {
    import c.universe.*
    val builder = c.prefix.tree match {
      case Apply(_, List(value)) => value
      case _                     => c.abort(c.enclosingPosition, "Expected a policy builder")
    }
    q"""val sp1 = fields.FieldPath.sub($selector1)
        val sp2 = fields.FieldPath.sub($selector2)
        val sp3 = fields.FieldPath.sub($selector3)
        $builder.mappedRule(_.downS(sp1, $selector1), _.downS(sp2, $selector2), _.downS(sp3, $selector3))(..$rules)"""
  }
}
