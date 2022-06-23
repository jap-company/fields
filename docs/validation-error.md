# Error

This library comes with some predefined error types and some of them may suit you needs.
They all are in `jap.fields.error` package check them out.

Don`t worry if none of them is what you need you can use your error type

## ValidationError

Error type that has path, error type and optional message.
Every error type is separate class so you can easily match on it.

## ValidationMessage

Error type that has path, error type and optional message. Thats all nothing special here.

## FieldError

Error type that has path and generic error that can be anything you want.
For example your errors are ussualy just error codes you may use `FieldError[Int]` as your error type to carry both path and error code.
