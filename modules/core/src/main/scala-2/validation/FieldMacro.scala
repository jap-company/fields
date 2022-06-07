package jap.fields

import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.blackbox

object FieldMacro {
  def fromSubMacro[A: c.WeakTypeTag](c: blackbox.Context)(value: c.Expr[A]): c.Expr[Field[A]] = {
    import c.universe._

    def selectorPath(tree: Tree): List[String] = tree match {
      case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => selectorPath(rest) :+ name.toString
      case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => selectorPath(rest) :+ name.toString
      case Select(rest, name: TermName)                                          => selectorPath(rest) :+ name.toString
      case Ident(TermName(_))                                                    => Nil
      case This(_)                                                               => Nil
      case got => c.abort(c.enclosingPosition, "Field.from only support variable selector")
    }

    // c.info(c.enclosingPosition, showRaw(value.tree), true)
    val path = q"jap.fields.FieldPath(${selectorPath(value.tree).map(n => Literal(Constant(n)))})"
    c.Expr[Field[A]](q"""jap.fields.Field($path, $value)""")
  }

  def fromMacro[A: c.WeakTypeTag](c: blackbox.Context)(value: c.Expr[A]): c.Expr[Field[A]] = {
    import c.universe._

    def selectorPath(tree: Tree): List[String] = tree match {
      case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => selectorPath(rest) :+ name.toString
      case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => selectorPath(rest) :+ name.toString
      case Select(rest, name: TermName)                                          => selectorPath(rest) :+ name.toString
      case Ident(TermName(name))                                                 => List(name)
      case This(_)                                                               => Nil
      case got => c.abort(c.enclosingPosition, "Field.from only support variables or variable selector")
    }

    // c.info(c.enclosingPosition, showRaw(value.tree), true)
    val path = q"jap.fields.FieldPath(${selectorPath(value.tree).map(n => Literal(Constant(n)))})"
    c.Expr[Field[A]](q"""jap.fields.Field($path, $value)""")
  }

  def subMacro[P, S](c: blackbox.Context)(selector: c.Expr[P => S]): c.Expr[Field[S]] = {
    import c.universe._
    def selectorPath(tree: Tree): List[String] = tree match {
      case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => selectorPath(rest) :+ name.toString
      case Apply(Select(rest, TermName("apply")), List(Literal(Constant(name)))) => selectorPath(rest) :+ name.toString
      case Select(rest, name: TermName)                                          => selectorPath(rest) :+ name.toString
      case Ident(TermName(name))                                                 => Nil
      case This(_)                                                               => Nil
      case got => c.abort(c.enclosingPosition, "Function is not chain of transformations")
    }

    val q"($_) => $body"        = selector.tree
    val q"$_.$_[..$_]($parent)" = c.prefix.tree
    // c.info(c.enclosingPosition, showRaw(body), true)

    val path = q"jap.fields.FieldPath(${selectorPath(body).map(n => Literal(Constant(n)))})"
    c.Expr[Field[S]](q"""jap.fields.Field($parent.path ++ $path, $selector($parent.value))""")
  }

  def policySubRuleMacro[P, S, F[_], VR[_], E](
      c: blackbox.Context
  )(selector: c.Expr[P => S])(rules: c.Tree*): c.Tree = {
    import c.universe._
    val q"$_.$_[..$_]($builder)" = c.prefix.tree
    q"$builder.fieldRule(_.sub($selector))(..$rules)"
  }

  def policySubRule2Macro[P, S1, S2, F[_], VR[_], E](
      c: blackbox.Context
  )(selector1: c.Expr[P => S1], selector2: c.Expr[P => S2])(rules: c.Tree*): c.Tree = {
    import c.universe._
    val q"$_.$_[..$_]($builder)" = c.prefix.tree
    q"$builder.fieldRule2(_.sub($selector1), _.sub($selector2))(..$rules)"
  }
}
