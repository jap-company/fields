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
package error

/** This corresponds to `error` field of ValidationError with given names */
object ValidationTypes {
  val Invalid          = "invalid_error"
  val Empty            = "empty_error"
  val NonEmpty         = "non_empty_error"
  val Greater          = "greater_error"
  val GreaterEqual     = "greater_equal_error"
  val Less             = "less_error"
  val LessEqual        = "less_equal_error"
  val Equal            = "equal_error"
  val NotEqual         = "not_equal_error"
  val MinSize          = "min_size_error"
  val MaxSize          = "max_size_error"
  val OneOf            = "one_of_error"
  val StringStartsWith = "string/starts_with"
  val StringEndsWith   = "string/ends_with"
  val StringMatch      = "string/match"
}
