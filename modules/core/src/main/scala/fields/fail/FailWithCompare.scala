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
package fail

import scala.annotation.implicitNotFound

import typeclass.FieldCompare
import error.ValidationTypes

@implicitNotFound("To use this operation you need to have FailWithCompare[${E}] in scope")
trait FailWithCompare[E, +P] {
  def compare[PP >: P](operation: CompareOperation, compared: String)(field: Field[PP]): E

  def compare[PP >: P, C](
      operation: CompareOperation,
      compared: C,
  )(field: Field[PP])(implicit C: FieldCompare[PP, C]): E =
    compare[PP](operation, C.show(compared))(field)

  def notEqual[PP >: P, C](compared: C)(field: Field[PP])(implicit C: FieldCompare[PP, C]): E =
    compare[PP, C](CompareOperation.NotEqual, compared)(field)

  def equal[PP >: P, C](compared: C)(field: Field[PP])(implicit C: FieldCompare[PP, C]): E =
    compare[PP, C](CompareOperation.Equal, compared)(field)

  def less[PP >: P, C](compared: C)(field: Field[PP])(implicit C: FieldCompare[PP, C]): E =
    compare[PP, C](CompareOperation.Less, compared)(field)

  def lessEqual[PP >: P, C](compared: C)(field: Field[PP])(implicit C: FieldCompare[PP, C]): E =
    compare[PP, C](CompareOperation.LessEqual, compared)(field)

  def greaterEqual[PP >: P, C](compared: C)(field: Field[PP])(implicit C: FieldCompare[PP, C]): E =
    compare[PP, C](CompareOperation.GreaterEqual, compared)(field)

  def greater[PP >: P, C](compared: C)(field: Field[PP])(implicit C: FieldCompare[PP, C]): E =
    compare[PP, C](CompareOperation.Greater, compared)(field)
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
}
object CompareOperation       {
  case object Equal        extends CompareOperation
  case object NotEqual     extends CompareOperation
  case object Greater      extends CompareOperation
  case object GreaterEqual extends CompareOperation
  case object Less         extends CompareOperation
  case object LessEqual    extends CompareOperation
}
