package jap.fields

case class FieldPath(value: List[String]) extends AnyVal {
  def isRoot       = value.isEmpty
  // ToDo: how to name root? Should it be ""?
  def full: String = if (isRoot) "root" else value.mkString(".")
  def name: String = value.lastOption.getOrElse("root")

  def named(name: String): FieldPath = FieldPath(value.dropRight(1) :+ name)
  def ++(path: FieldPath): FieldPath = FieldPath(value ++ path.value)
  def +(path: String): FieldPath     = FieldPath(value :+ path)

  override def toString: String = full
}

object FieldPath {
  val root                             = FieldPath()
  def apply(value: String*): FieldPath = FieldPath(value.toList)
  def raw(path: String)                = FieldPath(path.split('.').toList)

  // ToDo: should there be implicit conversions or should this just be factory methods
  implicit def fromString(path: String): FieldPath     = FieldPath(path :: Nil)
  implicit def fromList(path: List[String]): FieldPath = FieldPath(path)
  implicit def fromField[P](f: Field[P]): FieldPath    = f.path
}
