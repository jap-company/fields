package jap.fields

trait ValidationPolicy[P, F[_], VR[_], E] { self =>
  def validate(field: Field[P]): F[VR[E]]
}

object ValidationPolicy {
  def builder[P, F[_], VR[_], E](implicit M: ValidationModule[F, VR, E]): ValidationPolicyBuilder[P, F, VR, E] =
    ValidationPolicyBuilder()
}

case class ValidationPolicyBuilder[P, F[_], VR[_], E](rules: List[Field[P] => F[VR[E]]] = Nil)(implicit
    val M: ValidationModule[F, VR, E]
) {
  def rule(r: Field[P] => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] = copy(rules = rules ++ r)

  def validate(field: Field[P]): F[VR[E]] =
    M.combineAll(rules.map(_.apply(field)))

  def build: ValidationPolicy[P, F, VR, E] = field => M.combineAll(rules.map(_.apply(field)))
}
