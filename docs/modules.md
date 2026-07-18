# Modules and compatibility

Fields v1 separates the validation front ends from the small shared core.

| Module | Purpose | Scala versions | Main external dependencies |
|---|---|---|---|
| `@coreModuleName@` | Rules, policies, effects, validation containers, paths, errors, and Circe decoder syntax | @scalaPublishVersions@ | Circe is optional |
| `@valueModuleName@` | Value-carrying `Field` DSL | @scalaPublishVersions@ | Core only |
| `@lensModuleName@` | Reusable lens/policy DSL and path macros | @scalaPublishVersions@ | Core only |
| `@catsModuleName@` | Cats `Effect`, `Validated`, `Chain`, `Eval`, and error-container integration | @scalaPublishVersions@ | Cats Core |
| `@zioModuleName@` | ZIO effect and validation helpers | @scalaPublishVersions@ | ZIO 2 |
| `@zioBlocksSchemaModuleName@` | Native ZIO Blocks `SchemaError` instances and policy-backed schema validation for Value and Lens | 2.13 and 3 | Fields Core and ZIO Blocks Schema |

Add only the front end and integrations your application uses. Transitive dependencies provide core, but depending on core explicitly is useful when its types are part of your public API.

```scala
libraryDependencies ++= Seq(
  "@organization@" %% "@coreModuleName@" % "@version@",
  "@organization@" %% "@valueModuleName@" % "@version@",
  "@organization@" %% "@lensModuleName@" % "@version@",
  "@organization@" %% "@catsModuleName@" % "@version@",   // optional
  "@organization@" %% "@zioModuleName@" % "@version@",    // optional
  "@organization@" %% "@zioBlocksSchemaModuleName@" % "@version@", // optional integration, Scala 2.13/3
)
```

## Choosing a front end

- Choose **Value** when validation is local and direct access to the current value/path makes rules easiest to write.
- Choose **Lens** when policies should be reusable without wrapping the validated model in `Field` values.

Both use the same core `RuleK`, `PolicyK`, `Effect`, `Validated`, path, and error concepts.
