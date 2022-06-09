package jap.fields

import scala.annotation.tailrec
import scala.quoted.*
import scala.runtime.SymbolLiteral

class SelectorMacro[Q <: Quotes](using val q: Q) {
  import q.reflect.*

  def stringExpr(value: String) = Literal(StringConstant(value)).asExprOf[String]

  // Recursively extracts names from call chain
  def selectorPath(term: Term, includeIdent: Boolean, title: String): Expr[List[String]] = {
    @tailrec
    def go(term: Term, acc: List[String] = Nil): List[String] =
      term match {
        case This(_)                                                          => Nil
        case Ident(name)                                                      => if (includeIdent) name :: acc else acc
        case Select(rest, name)                                               => go(rest, name.toString :: acc)
        case Inlined(_, _, rest)                                              => go(rest, acc)
        case Apply(Select(rest, "apply"), List(Literal(IntConstant(index))))  => go(rest, index.toString :: acc)
        case Apply(Select(rest, "apply"), List(Literal(StringConstant(key)))) => go(rest, key :: acc)
        case unmatched => report.throwError(FieldMacroMessage.selectorErrorMessage(title))
      }

    // report.info(term.show(using Printer.TreeStructure))
    Expr.ofList(go(term).map(stringExpr))
  }
}

object FieldMacro {
  def fromImpl[P: Type](value: Expr[P], includeIdent: Boolean, title: String)(using q: Quotes) = {
    import quotes.reflect.*
    // report.info(value.asTerm.show(using Printer.TreeStructure))
    val parts = new SelectorMacro[q.type].selectorPath(value.asTerm, includeIdent, title)
    '{
      Field(
        path = FieldPath(${ parts }),
        value = $value,
      )
    }
  }

  def subImpl[P: Type, S: Type](parent: Expr[Field[P]], subSelector: Expr[P => S])(using q: Quotes): Expr[Field[S]] = {
    import quotes.reflect.*
    def extractBody(body: Term): Term =
      body match
        case Inlined(_, _, term)                         => extractBody(term)
        case Block(List(DefDef(_, _, _, Some(term))), _) => term
        case _ => report.throwError("This is not a selector function. Check the Documentation")
    // Get body
    val selectorBody                  = extractBody(subSelector.asTerm)
    // report.info(selectorBody.asTerm.show(using Printer.TreeStructure))
    val parts = new SelectorMacro[q.type].selectorPath(selectorBody, includeIdent = false, title = "Field#sub")
    '{
      Field(
        path = $parent.path ++ FieldPath(${ parts }),
        value = $subSelector($parent.value),
      )
    }
  }
}
