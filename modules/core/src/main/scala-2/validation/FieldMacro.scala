package jap.fields

import scala.annotation.tailrec
import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.blackbox

class FieldMacro(val c: blackbox.Context) {
  import c.universe._

  def selectorPath(tree: Tree, includeIdent: Boolean, title: String): List[String] = {
    @tailrec
    def go(tree: Tree, acc: List[String] = Nil): List[String] =
      tree match {
        case This(_)                      => Nil
        case Ident(TermName(name))        => if (includeIdent) name :: acc else acc
        case Select(rest, name: TermName) => go(rest, name.toString :: acc)
        case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => go(rest, name.toString :: acc)
        case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => go(rest, name.toString :: acc)
        case got => c.abort(c.enclosingPosition, FieldMacroMessage.selectorErrorMessage(title))
      }

    go(tree)
  }

  def fromSubMacro[A: c.WeakTypeTag](value: c.Expr[A]): c.Expr[Field[A]] = {
    // c.info(c.enclosingPosition, showRaw(value.tree), true)
    val parts = selectorPath(value.tree, includeIdent = false, title = "Field.sub")
    val path  = q"jap.fields.FieldPath(${parts.map(n => Literal(Constant(n)))})"
    c.Expr[Field[A]](q"""jap.fields.Field($path, $value)""")
  }

  def fromMacro[A: c.WeakTypeTag](value: c.Expr[A]): c.Expr[Field[A]] = {
    // c.info(c.enclosingPosition, showRaw(value.tree), true)
    val parts = selectorPath(value.tree, includeIdent = true, title = "Field.from")
    val path  = q"jap.fields.FieldPath(${parts.map(n => Literal(Constant(n)))})"
    c.Expr[Field[A]](q"""jap.fields.Field($path, $value)""")
  }

  def subMacro[P, S](selector: c.Expr[P => S]): c.Expr[Field[S]] = {
    import c.universe._
    val q"($_) => $body"        = selector.tree
    val q"$_.$_[..$_]($parent)" = c.prefix.tree
    // c.info(c.enclosingPosition, showRaw(body), true)
    val parts                   = selectorPath(body, includeIdent = false, title = "Field#sub")
    val path                    = q"jap.fields.FieldPath(${parts.map(n => Literal(Constant(n)))})"
    c.Expr[Field[S]](q"""jap.fields.Field($parent.path ++ $path, $selector($parent.value))""")
  }

  def policySubRuleMacro[P, S, F[_], VR[_], E](selector: c.Expr[P => S])(rules: c.Tree*): c.Tree = {
    import c.universe._
    val q"$_.$_[..$_]($builder)" = c.prefix.tree
    q"$builder.fieldRule(_.sub($selector))(..$rules)"
  }

  def policySubRule2Macro[P, S1, S2, F[_], VR[_], E](selector1: c.Expr[P => S1], selector2: c.Expr[P => S2])(rules: c.Tree*): c.Tree = {
    import c.universe._
    val q"$_.$_[..$_]($builder)" = c.prefix.tree
    q"$builder.fieldRule2(_.sub($selector1), _.sub($selector2))(..$rules)"
  }
}
