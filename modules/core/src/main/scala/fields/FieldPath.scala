package jap.fields

/** [[jap.fields.FieldPath]] contains path parts of the Field.
  */
case class FieldPath(value: List[String]) extends AnyVal {

  /** Is current path root.
    */
  def isRoot = value.isEmpty

  /** Full name of the path is dot-separated parts of this path. For root path this will be "root" (Object of discussion
    * what this should be)
    */
  def full: String = if (isRoot) FieldPath.RootName else value.mkString(".")

  /** Name of the path is the last part of path.
    */
  def name: String = value.lastOption.getOrElse(FieldPath.RootName)

  /** Changes name of this path
    */
  def named(name: String): FieldPath = FieldPath(value.dropRight(1) :+ name)

  /** Append other [[jap.fields.FieldPath]] to current path
    */
  def ++(path: FieldPath): FieldPath = FieldPath(value ++ path.value)

  /** Append other path part to current path
    */
  def +(path: String): FieldPath = FieldPath(value :+ path)

  override def toString: String = full
}

object FieldPath {
  val RootName = "root"

  /** Root FieldPath
    */
  val Root = FieldPath()

  /** Create [[jap.fields.FieldPath]] from `parts`
    */
  def apply(parts: String*): FieldPath = FieldPath(parts.toList)

  /** Parse [[jap.fields.FieldPath]] from dot-separated `path` string
    */
  def raw(path: String) = FieldPath(path.split('.').toList)

  /** Conversion from [[String]]
    */
  implicit def fromString(path: String): FieldPath = FieldPath(path :: Nil)

  /** Conversion from [[List[String]]]
    */
  implicit def fromList(path: List[String]): FieldPath = FieldPath(path)

  /** Conversion from [[Field]]
    */
  implicit def fromField[P](f: Field[P]): FieldPath = f.path
}
