# Binding Result Position Companion

Warning icon on a Spring MVC controller method parameter annotated
`@Valid`/`@Validated` whose matching `BindingResult`/`Errors`
parameter exists somewhere in the method signature, but isn't the
parameter immediately following it — Spring's own
`ErrorsMethodArgumentResolver` javadoc is explicit: "An Errors method
argument is expected to appear immediately after the model attribute
in the method signature." When another parameter is inserted between
them, Spring can't resolve the binding result for that attribute at
all.

## Why it exists

`createOrder(@Valid OrderForm form, Model model, BindingResult
bindingResult)` compiles fine — but Spring can't associate that
`BindingResult` with `form`'s validation because `Model` sits between
them. The validation errors are silently lost (or, depending on
version, Spring throws at startup) — nothing about the code itself
looks wrong.

## Why built this way

- **100% static text/PSI analysis** — matches annotation/type names by
  simple text, so it works whether the real Spring jar is on the
  classpath or not. Java and Kotlin.
- **Confirmed gap**: JetBrains' own bundled Spring MVC plugin only
  offers autocompletion for `BindingResult` parameters
  (`BindingResultParameterProvider`) — confirmed by extracting and
  reading the plugin's own `plugin.xml` directly. It does not validate
  correct positioning.

## v0.1 scope — stated honestly, not exhaustively

Only flags the case where a `BindingResult`/`Errors` parameter DOES
exist somewhere in the method but isn't immediately next — a `@Valid`
parameter with no `BindingResult`/`Errors` at all is a legitimate,
common pattern (Spring throws `MethodArgumentNotValidException`
automatically in that case) and is never flagged.

## Usage

Open any Java/Kotlin Spring MVC controller. A `@Valid`/`@Validated`
parameter whose `BindingResult`/`Errors` exists but isn't immediately
next shows a warning icon.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
