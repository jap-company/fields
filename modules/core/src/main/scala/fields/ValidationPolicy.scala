/*
 * Copyright 2022 Jap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jap.fields

import typeclass._

/** Typeclass that defines how to validate given field */
trait ValidationPolicy[P, F[_], V[_], E] { self =>
  def validate(field: Field[P]): Rule[F, V, E]

  def apply(field: Field[P]): Rule[F, V, E] = validate(field)

  /** Validates `field` using Policy and returns it as Either.Left if invalid and Either.Right if valid */
  def validateEither(field: Field[P])(implicit F: Effect[F], V: Validated[V]): F[Either[V[E], P]] =
    F.map(validate(field).effect)(vr => if (V.isValid(vr)) Right(field.value) else Left(vr))
}

object ValidationPolicy {
  def builder[P, F[_]: Effect, V[_]: Validated, E]: ValidationPolicyBuilder[P, F, V, E] =
    ValidationPolicyBuilder()
}

/** Builder class for [[jap.fields.ValidationPolicy]]. [[jap.fields.ValidationModule]] implicit should be in scope */
case class ValidationPolicyBuilder[P, F[_]: Effect, V[_]: Validated, E](rules: List[Field[P] => Rule[F, V, E]] = Nil) {

  /** Adds new rule to builder */
  def rule(rule: Field[P] => Rule[F, V, E]*): ValidationPolicyBuilder[P, F, V, E] = copy(rules = rules ++ rule)

  /** Adds new sub field rule to builder. First extracts sub field using `sub` function and then applies all `rules` to
    * it
    */
  def fieldRule[S](sub: Field[P] => Field[S])(rules: Field[S] => Rule[F, V, E]*) =
    rule { f =>
      val subField = sub(f)
      Rule.andAll(rules.map(_.apply(subField)).toList)
    }

  /** Same as `fieldRule` but for 2 sub fields */
  def fieldRule[S1, S2](
      sub1: Field[P] => Field[S1],
      sub2: Field[P] => Field[S2],
  )(rules: (Field[S1], Field[S2]) => Rule[F, V, E]*) =
    rule { f =>
      val subField1 = sub1(f)
      val subField2 = sub2(f)
      Rule.andAll(rules.map(_.apply(subField1, subField2)).toList)
    }

  /** Same as `fieldRule` but for 3 sub fields */
  def fieldRule[S1, S2, S3](
      sub1: Field[P] => Field[S1],
      sub2: Field[P] => Field[S2],
      sub3: Field[P] => Field[S3],
  )(rules: (Field[S1], Field[S2], Field[S3]) => Rule[F, V, E]*) =
    rule { f =>
      val subField1 = sub1(f)
      val subField2 = sub2(f)
      val subField3 = sub3(f)
      Rule.andAll(rules.map(_.apply(subField1, subField2, subField3)).toList)
    }

  /** Applies all validaiton rules to [[jap.fields.Field]]#value */
  def build: ValidationPolicy[P, F, V, E] = field => Rule.andAll(rules.map(_.apply(field)))
}
