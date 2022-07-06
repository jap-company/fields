package jap.fields
package syntax

object all extends all
trait all
    extends GenericSyntax
    with BooleanSyntax
    with RuleSyntax
    with OrderingSyntax
    with OptionSyntax
    with StringSyntax
    with MapSyntax
    with IterableSyntax
    with FieldSyntax
    with ValidatedSyntax
    with PolicySyntax

trait ModuleAllSyntax[F[_], V[_], E]
    extends ModuleGenericSyntax[F, V, E]
    with ModuleBooleanSyntax[F, V, E]
    with ModuleOrderingSyntax[F, V, E]
    with ModuleOptionSyntax[F, V, E]
    with ModuleStringSyntax[F, V, E]
    with ModuleMapSyntax[F, V, E]
    with ModuleIterableSyntax[F, V, E]
