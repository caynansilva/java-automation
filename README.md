# PokeAPI Java API Automation

A small Java 21 and Maven project for learning API test automation against the public [PokeAPI](https://pokeapi.co/). Tests use JUnit 5 and follow the repository's BABELL flow. Rest Assured is kept behind the API and `Helper` classes. Cucumber is not used.

## Requirements

- JDK 21
- Apache Maven available as `mvn` on your command line
- Internet access to reach PokeAPI

Confirm Java and Maven are available from the project root:

```shell
java -version
mvn -version
```

## Run the tests

Run the complete suite from the directory containing `pom.xml`:

```shell
mvn test
```

Run a single numbered scenario from the same root directory:

```shell
mvn -Dtest=PK_001_API_TEST test
mvn -Dtest=PK_002_API_TEST test
mvn -Dtest=PK_003_API_TEST test
mvn -Dtest=PK_004_API_TEST test
mvn -Dtest=PK_005_API_TEST test
mvn -Dtest=PK_006_API_TEST test
mvn -Dtest=PK_007_API_TEST test
```

Clean compiled output and run the suite again:

```shell
mvn clean test
```

Surefire is configured in `pom.xml` to discover tests named `PK_*_API_TEST`. No `package.json` or Node.js setup is needed for this Maven project.

## Scenarios

| Test | Coverage |
|------|----------|
| `PK_001_API_TEST` | Request Pikachu by name; check its name and HTTP 200. |
| `PK_002_API_TEST` | Request Pokémon ID 25; check Pikachu, ID 25, and HTTP 200. |
| `PK_003_API_TEST` | Request an unknown Pokémon and check HTTP 404. |
| `PK_004_API_TEST` | Check Pikachu's response fields and required abilities, types, and stats. |
| `PK_005_API_TEST` | Check Bulbasaur has grass and poison types. |
| `PK_006_API_TEST` | Check Pikachu has the static ability. |
| `PK_007_API_TEST` | Check HTTP 200 and the JSON response content type. |

## Project structure

```text
src/
├── main/java/com/caynan/qa/
│   ├── api/       # PokeAPI endpoint operations
│   ├── types/     # RequestType and typed PokemonResponse data
│   └── utils/     # Shared HTTP and response operations
└── test/java/
    ├── api/       # Small numbered JUnit scenario classes
    └── test_steps/ # BDD steps and shared reusable test operations
```

The tests call BDD-style methods in their Steps classes. Reusable operations keep the response state, call `PokemonAPI` and `Helper`, and use `PokemonResponse` for typed field checks. Tests do not build HTTP requests with Rest Assured.

## Dependencies

The Maven dependencies and Java version are declared in `pom.xml`:

- Java 21
- JUnit Jupiter 5
- REST Assured
- Jackson Databind for mapping JSON into `PokemonResponse`

The suite calls the live PokeAPI, so network outages or API availability can affect test results.
