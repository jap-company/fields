package jap.fields

/** Typeclass that defines how to validate given field */
trait ValidationPolicy[P, F[_], VR[_], E] { self =>
  def validate(field: Field[P]): F[VR[E]]
}

object ValidationPolicy {
  def builder[P, F[_], VR[_], E](implicit M: ValidationModule[F, VR, E]): ValidationPolicyBuilder[P, F, VR, E] =
    ValidationPolicyBuilder()
}

/** Builder class for [[jap.fields.ValidationPolicy]]. [[jap.fields.ValidationModule]] implicit should be in scope */
case class ValidationPolicyBuilder[P, F[_], VR[_], E](rules: List[Field[P] => F[VR[E]]] = Nil)(implicit
    val M: ValidationModule[F, VR, E]
) {

  /** Adds new rule to builder */
  def rule(r: Field[P] => F[VR[E]]*): ValidationPolicyBuilder[P, F, VR, E] = copy(rules = rules ++ r)

  /** Adds new sub field rule to builder. First extracts sub field using `sub` function and then applies all `rules` to
    * it
    */
  def fieldRule[S](sub: Field[P] => Field[S])(rules: Field[S] => F[VR[E]]*) =
    rule { f =>
      val subField = sub(f)
      M.combineAll(rules.map(_.apply(subField)).toList)
    }

  /** Same as `fieldRule` but for 2 sub fields */
  def fieldRule2[S1, S2](
      sub1: Field[P] => Field[S1],
      sub2: Field[P] => Field[S2],
  )(
      rules: (Field[S1], Field[S2]) => F[VR[E]]*
  ) =
    rule { f =>
      val subField1 = sub1(f)
      val subField2 = sub2(f)
      M.combineAll(rules.map(_.apply(subField1, subField2)).toList)
    }

  /** Applies all validaiton rules to [[Field]]#value */
  def build: ValidationPolicy[P, F, VR, E] = field => M.combineAll(rules.map(_.apply(field)))
}
