# Contributing

Thanks for contributing to anything-goes-formatter-plugin.

## Prerequisites

- Java 11+
- Apache Maven 3.9+

## Build and Test

Run a full verification build:

```sh
mvn -ntp verify
```

Run the plugin in a local Jenkins instance:

```sh
mvn -ntp hpi:run
```

## Formatting (mandatory)

Before pushing changes, run:

```sh
mvn -ntp spotless:apply
```

All pull requests must include formatting changes if Spotless reports differences.

## Pull Request Guidelines

- Use a clear, imperative PR title.
- Add testing notes in the PR description.
- Link related GitHub issue(s) when applicable.
- Keep PRs focused and small when possible.

## Security

Please report vulnerabilities privately through Jenkins security channels instead of opening a public issue.
