package jap.fields

object FieldMacroMessage {
  def selectorErrorMessage(title: String) =
    s"""
       |$title
       |
       |The value must be a chain of selects or identifier.
       |
       |For example:
       |request.user.name
       |request
     """.stripMargin
}
