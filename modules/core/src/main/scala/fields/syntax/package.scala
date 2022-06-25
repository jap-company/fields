package jap.fields

package object syntax {
  object all
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
}
