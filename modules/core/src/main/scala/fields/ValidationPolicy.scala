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

  def fieldRule[S](sub: Field[P] => Field[S])(rules: Field[S] => F[VR[E]]*) =
    rule { f =>
      val subField = sub(f)
      M.combineAll(rules.map(_.apply(subField)))
    }

  def fieldRule2[S1, S2](
      sub1: Field[P] => Field[S1],
      sub2: Field[P] => Field[S2],
  )(
      rules: (Field[S1], Field[S2]) => F[VR[E]]*
  ) =
    rule { f =>
      val subField1 = sub1(f)
      val subField2 = sub2(f)
      M.combineAll(rules.map(_.apply(subField1, subField2)))
    }

  def validate(field: Field[P]): F[VR[E]] =
    M.combineAll(rules.map(_.apply(field)))

  def build: ValidationPolicy[P, F, VR, E] = field => M.combineAll(rules.map(_.apply(field)))
}
