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

import ZioBlocksSchemaSyntax.*
import fields.typeclass.Effect
import zio.blocks.schema.{Schema, SchemaError}

class ZioBlocksSchemaInteropSuite extends munit.FunSuite {
  private object Validation extends value.FieldsDsl.BaseAccumulate[Effect.Sync, SchemaError]
    with ZioBlocksSchemaErrorDsl
  import Validation.*

  private object LensValidation extends lens.FieldsDsl.BaseAccumulate[Effect.Sync, SchemaError]
    with ZioBlocksSchemaErrorDsl

  private val policy: Policy[List[String]] = field =>
    Rule.andAll(
      field.value.zipWithIndex.map { case (value, index) =>
        field.down(index, value).nonEmpty
      }
    )

  test("native DSL errors retain Fields paths") {
    val error = Field(FieldPath(FieldPart.Path("users"), FieldPart.Index(2)), "").failEmpty

    assertEquals(error.errors.length, 1)
    assertEquals(error.errors.head.source.toString, ".users[2]")
  }

  test("mounted policy emits accumulated native SchemaError values while decoding") {
    val schema  = Schema[List[String]].withPolicy(policy)
    val invalid = List("", "valid", "")
    val result  = schema.fromDynamicValue(schema.toDynamicValue(invalid))

    result match {
      case Left(error: SchemaError) =>
        error.errors.head match {
          case SchemaError.ConversionFailed(source, details, None) =>
            assertEquals(source.toString, ".~")
            assert(details.contains("must not be empty at: [0]"))
            assert(details.contains("must not be empty at: [2]"))
          case value => fail(s"Expected native conversion failure, got $value")
        }
      case value                    => fail(s"Expected native SchemaError, got $value")
    }
  }

  test("direct policy validation preserves accumulated native error structure") {
    val schema = Schema[List[String]]
    val result = schema.validateWithPolicy(policy)(List("", "valid", ""))

    result match {
      case Left(error) =>
        assertEquals(error.errors.length, 2)
        assertEquals(error.errors.map(_.source.toString), List("[0]", "[2]"))
      case value       => fail(s"Expected native SchemaError, got $value")
    }
  }

  test("mounted policy returns decoded values when valid") {
    val schema = Schema[List[String]].withPolicy(policy)
    val value  = List("first", "second")

    assertEquals(schema.fromDynamicValue(schema.toDynamicValue(value)), Right(value))
  }

  test("lens policy validation preserves accumulated native paths") {
    import LensValidation.*

    val lensPolicy: LensValidation.LensPolicy[List[String]] =
      LensPolicy.root[List[String]](_.each(FieldLens[String].nonEmpty))
    val result = Schema[List[String]].validateWithLensPolicy(lensPolicy.root)(List("", "valid", ""))

    result match {
      case Left(error) =>
        assertEquals(error.errors.length, 2)
        assertEquals(error.errors.map(_.source.toString), List("[0]", "[2]"))
      case value       => munit.Assertions.fail(s"Expected native SchemaError, got $value")
    }
  }

  test("mounted lens policy validates schema decoding") {
    import LensValidation.*

    val lensPolicy: LensValidation.LensPolicy[String] =
      LensPolicy.root[String](_.nonEmpty)
    val policy: LensValidation.Policy[String]         = FieldLens[String].nonEmpty
    val schema                                        = Schema[String].withLensPolicy(lensPolicy.root)

    assertEquals(schema.fromDynamicValue(schema.toDynamicValue("valid")), Right("valid"))
    assert(schema.fromDynamicValue(schema.toDynamicValue("")).isLeft)
    assert(Schema[String].validateWithLensPolicy(policy)("").isLeft)
  }
}
