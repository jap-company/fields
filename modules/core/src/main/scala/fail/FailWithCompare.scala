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

package fields
package fail

import error.*

import scala.annotation.implicitNotFound

@implicitNotFound("To use this operation you need to have FailWithCompare[${E}] in scope")
trait FailWithCompare[E, +P] {
  def compare[PP >: P](operation: CompareOperation, compared: String)(path: FieldPath, value: PP): E

  def compare[PP >: P, C](
      operation: CompareOperation,
      compared: C,
  )(path: FieldPath, value: PP)(implicit C: CompareShow[PP, C]): E =
    compare[PP](operation, C.show(compared))(path, value)

  def notEqual[PP >: P, C](compared: C)(path: FieldPath, value: PP)(implicit C: CompareShow[PP, C]): E =
    compare[PP, C](CompareOperation.NotEqual, compared)(path, value)

  def equal[PP >: P, C](compared: C)(path: FieldPath, value: PP)(implicit C: CompareShow[PP, C]): E =
    compare[PP, C](CompareOperation.Equal, compared)(path, value)

  def less[PP >: P, C](compared: C)(path: FieldPath, value: PP)(implicit C: CompareShow[PP, C]): E =
    compare[PP, C](CompareOperation.Less, compared)(path, value)

  def lessEqual[PP >: P, C](compared: C)(path: FieldPath, value: PP)(implicit C: CompareShow[PP, C]): E =
    compare[PP, C](CompareOperation.LessEqual, compared)(path, value)

  def greaterEqual[PP >: P, C](compared: C)(path: FieldPath, value: PP)(implicit C: CompareShow[PP, C]): E =
    compare[PP, C](CompareOperation.GreaterEqual, compared)(path, value)

  def greater[PP >: P, C](compared: C)(path: FieldPath, value: PP)(implicit C: CompareShow[PP, C]): E =
    compare[PP, C](CompareOperation.Greater, compared)(path, value)
}

sealed trait CompareOperation {
  def constraint: String = this match {
    case CompareOperation.Equal        => ValidationTypes.Equal
    case CompareOperation.NotEqual     => ValidationTypes.NotEqual
    case CompareOperation.Greater      => ValidationTypes.Greater
    case CompareOperation.GreaterEqual => ValidationTypes.GreaterEqual
    case CompareOperation.Less         => ValidationTypes.Less
    case CompareOperation.LessEqual    => ValidationTypes.LessEqual
  }

  def message(compared: String) = this match {
    case CompareOperation.Equal        => ValidationMessages.Equal(compared)
    case CompareOperation.NotEqual     => ValidationMessages.NotEqual(compared)
    case CompareOperation.Greater      => ValidationMessages.Greater(compared)
    case CompareOperation.GreaterEqual => ValidationMessages.GreaterEqual(compared)
    case CompareOperation.Less         => ValidationMessages.Less(compared)
    case CompareOperation.LessEqual    => ValidationMessages.LessEqual(compared)
  }
}
object CompareOperation       {
  case object Equal        extends CompareOperation
  case object NotEqual     extends CompareOperation
  case object Greater      extends CompareOperation
  case object GreaterEqual extends CompareOperation
  case object Less         extends CompareOperation
  case object LessEqual    extends CompareOperation
}

@implicitNotFound("Cannot compare ${P} with ${C}")
trait CompareShow[P, C] {
  def show(compared: C): String
}

object CompareShow extends CompareShowInstances0 with CompareShowInstances1

trait CompareShowInstances1 {
  implicit def valueCompareShow[P, C <: P]: CompareShow[P, C] = _value.asInstanceOf[CompareShow[P, C]]
  val _value: CompareShow[Any, Any]                           = _.toString
}

trait CompareShowInstances0 {
  import fields.lens.FieldLens as LField
  import fields.value.Field as VField

  implicit def valueFieldCompareShow[P, C <: P]: CompareShow[P, VField[C]] =
    _valueField.asInstanceOf[CompareShow[P, VField[C]]]
  val _valueField: CompareShow[Any, VField[Any]]                           = _.path.full

  implicit def lensFieldCompareShow[P, C, CC <: C]: CompareShow[C, LField[P, CC]] =
    _lensField.asInstanceOf[CompareShow[C, LField[P, CC]]]
  val _lensField: CompareShow[Any, LField[Any, Any]]                              = _.path.full
}
