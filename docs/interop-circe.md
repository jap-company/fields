# Circe Interop

Fields provides interop with circe so you can add validation step to Decoder.
Just import all from CirceInterop and now you can use `usePolicy` extension method to add validation to you Decoder or Codec.

Note that this is only limited to `F[_]` that are Sync like `Effect.Sync` or cats `Eval`. To support other `F[_]` implement `RunSync` for it.
Also path is extracted using `HasFieldPath` type class, so when using custom errors do not forget to implement it, too.

```scala mdoc
import io.circe._
import jap.fields.CatsInterop.DefaultValidatedNelVM._
import jap.fields.CirceInterop._
import jap.fields._

case class Request(name: String)
object Request {
  implicit val policy: Policy[Request] =
    Policy
      .builder[Request]
      .subRule(_.name)(
        _.minSize(4),
        _.ensure(_ != "Rag", _.failMessage("Cannot be Rag")),
      )
      .build

  implicit val decoder: Decoder[Request] = Decoder.forProduct1("name")(Request.apply).usePolicy(policy)
}

val json = Json.obj("name" -> Json.fromString("Rag"))
Request.decoder.decodeAccumulating(json.hcursor)
```
