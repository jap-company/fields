package jap.fields

import scala.quoted.*
import scala.runtime.SymbolLiteral

object FieldMacro:
  def stringExpr(value: String)(using Quotes) =
    import quotes.reflect.*
    Literal(StringConstant(value)).asExprOf[String]

  def fromImpl[P](value: Expr[P])(using Type[P], Quotes) =
    import quotes.reflect.*

    // Recursively extracts names from call chain
    def extractNames(body: Term): List[String] = body match
      case Apply(Select(rest, "apply"), List(Literal(IntConstant(index))))  => extractNames(rest) :+ index.toString
      case Apply(Select(rest, "apply"), List(Literal(StringConstant(key)))) => extractNames(rest) :+ key
      case Inlined(_, _, term)                                              => extractNames(term)
      case Select(rest, name)                                               => extractNames(rest) :+ name.toString
      case Ident(name)                                                      => List(name)
      case unmatched => report.throwError("Pass a variable/selector like request or request.password")

    // report.info(value.asTerm.show(using Printer.TreeStructure))
    val names = extractNames(value.asTerm).map(stringExpr)

    '{
      Field(
        path = FieldPath(${ Expr.ofList(names) }),
        value = $value,
      )
    }

  def subImpl[P, S](
      parent: Expr[Field[P]],
      subSelector: Expr[P => S],
  )(using Type[P], Type[S], Quotes): Expr[Field[S]] =
    import quotes.reflect.*

    def extractBody(body: Term): Term =
      body match
        case Inlined(_, _, term)                         => extractBody(term)
        case Block(List(DefDef(_, _, _, Some(term))), _) => term
        case _ => report.throwError("This is not a selector function. Check the Documentation")

    // Get body
    val selectorBody = extractBody(subSelector.asTerm)

    // Recursively extracts names from call chain
    def extractNames(body: Term): List[String] = body match
      case Apply(Select(rest, "apply"), List(Literal(IntConstant(index))))  => extractNames(rest) :+ index.toString
      case Apply(Select(rest, "apply"), List(Literal(StringConstant(key)))) => extractNames(rest) :+ key
      case Select(rest, name)                                               => extractNames(rest) :+ name.toString
      case Ident(_)                                                         => List.empty
      case unmatched => report.throwError("Selector function should only include field selection like _.user.age")

    // report.info(selectorBody.asTerm.show(using Printer.TreeStructure))
    val path = extractNames(selectorBody).map(stringExpr)

    '{
      Field(
        path = $parent.path ++ FieldPath(${ Expr.ofList(path) }),
        value = $subSelector($parent.value),
      )
    }
