<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Binding Result Position Companion Changelog

## [Unreleased]

## [0.1.0]

### Added

- Warning icon on a @Valid/@Validated Spring MVC parameter whose
  BindingResult/Errors exists in the method signature but isn't the
  parameter immediately following it -- Spring's own javadoc says the
  Errors argument must appear immediately after, or it can't be
  resolved.
- 100% static text/PSI analysis, Java and Kotlin, no network calls,
  no telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/binding-result-position-companion/compare/0.1.0...HEAD
[0.1.0]: https://github.com/GapHunterLabs/binding-result-position-companion/commits/0.1.0
