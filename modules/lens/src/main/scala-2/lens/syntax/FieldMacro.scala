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
package lens
package syntax

import scala.reflect.macros.blackbox

class FieldMacro(val c: blackbox.Context) {
  def subMacro[P, S, SS](selector: c.Expr[S => SS]): c.Expr[FieldLens[P, SS]] = {
    import c.universe.*
    val body   = selector.tree match {
      case Function(_, value) => value
      case _                  => c.abort(c.enclosingPosition, "Expected a function selector")
    }
    val parent = c.prefix.tree match {
      case Apply(_, List(value)) => value
      case _                     => c.abort(c.enclosingPosition, "Expected a field lens")
    }
    // c.info(c.enclosingPosition, showRaw(body), true)
    c.Expr[FieldLens[P, SS]](q"""$parent.down(fields.FieldPath.sub($body), $selector)""")
  }

  def inferBasePath: c.Tree = {
    import c.universe.*

    val fieldLensType = appliedType(typeOf[FieldLens[Any, Any]].typeConstructor, List(WildcardType, WildcardType))
    val lensPath      = Option(c.inferImplicitValue(fieldLensType)).filter(_.nonEmpty).map(l => q"$l.path")

    val fieldPath =
      Option(c.inferImplicitValue(weakTypeOf[FieldPath])).filter(_.nonEmpty)

    lensPath.orElse(fieldPath).getOrElse(q"fields.FieldPath.Root")
  }

  def policySubRuleMacro[P, S, F[_], V[_], E](
      selector: c.Expr[P => S]
  )(rules: c.Tree*)(implicit P: c.WeakTypeTag[P]): c.Tree = {
    import c.universe.*
    val builder = c.prefix.tree match {
      case Apply(_, List(value)) => value
      case _                     => c.abort(c.enclosingPosition, "Expected a policy builder")
    }
    q"""val sl = fields.lens.FieldLens[$P]($inferBasePath).sub($selector)
        PolicyK.combine($builder, ..${rules.map(rule => q"$rule(sl)")})"""
  }

  def policySubRule2Macro[P, S1, S2, F[_], V[_], E](selector1: c.Expr[P => S1], selector2: c.Expr[P => S2])(
      rules: c.Tree*
  )(implicit P: c.WeakTypeTag[P]): c.Tree = {
    import c.universe.*
    val builder  = c.prefix.tree match {
      case Apply(_, List(value)) => value
      case _                     => c.abort(c.enclosingPosition, "Expected a policy builder")
    }
    val basePath = inferBasePath
    q"""val sl1 = fields.lens.FieldLens[$P]($basePath).sub($selector1)
        val sl2 = fields.lens.FieldLens[$P]($basePath).sub($selector2)
        PolicyK.combine($builder, ..${rules.map(rule => q"$rule(sl1, sl2)")})"""
  }

  def policySubRule3Macro[P, S1, S2, S3, F[_], V[_], E](
      selector1: c.Expr[P => S1],
      selector2: c.Expr[P => S2],
      selector3: c.Expr[P => S3],
  )(rules: c.Tree*)(implicit P: c.WeakTypeTag[P]): c.Tree = {
    import c.universe.*
    val builder  = c.prefix.tree match {
      case Apply(_, List(value)) => value
      case _                     => c.abort(c.enclosingPosition, "Expected a policy builder")
    }
    val basePath = inferBasePath
    q"""val sl1 = fields.lens.FieldLens[$P]($basePath).sub($selector1)
        val sl2 = fields.lens.FieldLens[$P]($basePath).sub($selector2)
        val sl3 = fields.lens.FieldLens[$P]($basePath).sub($selector3)
        PolicyK.combine($builder, ..${rules.map(rule => q"$rule(sl1, sl2, sl3)")})"""
  }
}
