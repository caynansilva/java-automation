package tools.g2f;

import com.caynan.qa.tools.GherkinToFunctions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GherkinToFunctionsTest {
    @TempDir
    Path projectRoot;

    private Path featureDirectory;
    private GherkinToFunctions generator;

    @BeforeEach
    void createProjectLayout() throws IOException {
        featureDirectory = Files.createDirectories(projectRoot.resolve("src/test/resources/features"));
        Files.createDirectories(projectRoot.resolve("src/test/java"));
        generator = new GherkinToFunctions(projectRoot);
    }

    @Test
    void generatesOneTestDefinitionForAValidFeature() throws IOException {
        Path feature = feature("PK_008_API_TEST.feature", """
                @PK_008
                Feature: Validate Pokemon
                  Scenario: Validate Pikachu
                    Given I request Pokemon "pikachu"
                    Then the Pokemon name should contain "pikachu"
                    And the response status code should be 200
                """);

        GherkinToFunctions.GenerationResult result = generator.generate(feature, false);
        String generated = Files.readString(result.outputPath());

        assertEquals(projectRoot.resolve("src/test/java/tests/pk008/PK_008_API_TEST.java"), result.outputPath());
        assertEquals(3, result.bindingCount());
        assertTrue(generated.contains("package tests.pk008;"));
        assertTrue(generated.contains("@SelectClasspathResource(\"features/PK_008_API_TEST.feature\")"));
        assertTrue(generated.contains("@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = \"tests.pk008\")"));
        assertTrue(generated.contains("public void iRequestPokemon(String pokemon)"));
        assertTrue(generated.contains("public void pokemonNameShouldContain(String name)"));
        assertTrue(generated.contains("public void responseStatusCodeShouldBe(int statusCode)"));
        assertTrue(generated.contains("throw new UnsupportedOperationException"));
        assertFalse(generated.contains("RestAssured"));
        assertFalse(Files.exists(result.outputPath().resolveSibling("PK_008_API_TEST_STEPS.java")));
    }

    @Test
    void derivesCanonicalClassNameFromFeatureName() throws IOException {
        Path feature = feature("pk_009_api_test.feature", oneScenario("Given I start"));

        GherkinToFunctions.GenerationResult result = generator.generate(feature, false);

        assertEquals("PK_009_API_TEST.java", result.outputPath().getFileName().toString());
    }

    @Test
    void derivesLowercasePackageFromFeatureId() throws IOException {
        Path feature = feature("PK_010_API_TEST.feature", oneScenario("Given I start"));

        GherkinToFunctions.GenerationResult result = generator.generate(feature, false);

        assertTrue(Files.readString(result.outputPath()).startsWith("package tests.pk010;"));
    }

    @Test
    void derivesOutputPathUnderMatchingTestPackage() throws IOException {
        Path feature = feature("PK_011_API_TEST.feature", oneScenario("Given I start"));

        GherkinToFunctions.GenerationResult result = generator.generate(feature, false);

        assertEquals(projectRoot.resolve("src/test/java/tests/pk011/PK_011_API_TEST.java"), result.outputPath());
    }

    @Test
    void convertsQuotedTextToStringParameter() throws IOException {
        Path feature = feature("PK_012_API_TEST.feature", oneScenario("Given I request Pokemon \"pikachu\""));

        String generated = generate(feature);

        assertTrue(generated.contains("@Given(\"I request Pokemon {string}\")"));
        assertTrue(generated.contains("String pokemon"));
    }

    @Test
    void convertsIntegerLiteralToIntParameter() throws IOException {
        Path feature = feature("PK_013_API_TEST.feature", oneScenario("Then the response status code should be 200"));

        String generated = generate(feature);

        assertTrue(generated.contains("@Then(\"the response status code should be {int}\")"));
        assertTrue(generated.contains("int statusCode"));
    }

    @Test
    void convertsDecimalLiteralToDoubleParameter() throws IOException {
        Path feature = feature("PK_014_API_TEST.feature", oneScenario("Then the value should be 3.5"));

        String generated = generate(feature);

        assertTrue(generated.contains("@Then(\"the value should be {double}\")"));
        assertTrue(generated.contains("double decimalValue"));
    }

    @Test
    void createsCamelCaseMethodNamesFromStepTextWithoutLiteralValues() throws IOException {
        Path feature = feature("PK_015_API_TEST.feature", oneScenario("Given I request Pokemon \"pikachu\""));

        String generated = generate(feature);

        assertTrue(generated.contains("public void iRequestPokemon(String pokemon)"));
        assertFalse(generated.contains("pikachu"));
    }

    @Test
    void deduplicatesBindingsAfterParameterNormalization() throws IOException {
        Path feature = feature("PK_016_API_TEST.feature", oneScenario("""
                Given I request Pokemon "pikachu"
                And I request Pokemon "bulbasaur"
                """));

        String generated = generate(feature);

        assertEquals(1, occurrences(generated, "@Given(\"I request Pokemon {string}\")"));
        assertEquals(1, occurrences(generated, "public void iRequestPokemon("));
    }

    @Test
    void mapsAndAndButToThePreviousSemanticKeyword() throws IOException {
        Path feature = feature("PK_017_API_TEST.feature", oneScenario("""
                Then the Pokemon name should contain "pikachu"
                And the response status code should be 200
                But the error should contain "none"
                """));

        String generated = generate(feature);

        assertEquals(3, occurrences(generated, "@Then("));
        assertFalse(generated.contains("@And("));
        assertFalse(generated.contains("@But("));
    }

    @Test
    void includesBackgroundBindingsAndDeduplicatesScenarioBindings() throws IOException {
        Path feature = feature("PK_018_API_TEST.feature", """
                Feature: Background behavior
                  Background: Common request
                    Given I request Pokemon "pikachu"
                  Scenario: Verify response
                    Given I request Pokemon "bulbasaur"
                    Then the response status code should be 200
                """);

        String generated = generate(feature);

        assertEquals(1, occurrences(generated, "@Given(\"I request Pokemon {string}\")"));
        assertTrue(generated.contains("@Then(\"the response status code should be {int}\")"));
    }

    @Test
    void rejectsMalformedFeatureFilename() throws IOException {
        Path feature = feature("pokemon.feature", oneScenario("Given I start"));

        RuntimeException error = assertThrows(RuntimeException.class, () -> generator.generate(feature, false));

        assertTrue(error.getMessage().contains("PK_NNN_API_TEST.feature"));
    }

    @Test
    void rejectsFeaturesWithMultipleScenarios() throws IOException {
        Path feature = feature("PK_019_API_TEST.feature", """
                Feature: Multiple scenarios
                  Scenario: One
                    Given I start
                  Scenario: Two
                    Given I stop
                """);

        RuntimeException error = assertThrows(RuntimeException.class, () -> generator.generate(feature, false));

        assertTrue(error.getMessage().contains("contains 2 scenarios"));
        assertTrue(error.getMessage().contains("Split the scenarios"));
    }

    @Test
    void rejectsFeatureWithoutScenario() throws IOException {
        Path feature = feature("PK_020_API_TEST.feature", "Feature: No scenario\n");

        RuntimeException error = assertThrows(RuntimeException.class, () -> generator.generate(feature, false));

        assertTrue(error.getMessage().contains("No Scenario"));
    }

    @Test
    void rejectsScenarioOutlineAndExamples() throws IOException {
        Path feature = feature("PK_021_API_TEST.feature", """
                Feature: Outline
                  Scenario Outline: Try <pokemon>
                    Given I request Pokemon "<pokemon>"
                    Examples:
                      | pokemon |
                      | pikachu |
                """);

        RuntimeException error = assertThrows(RuntimeException.class, () -> generator.generate(feature, false));

        assertTrue(error.getMessage().contains("Scenario Outline / Examples"));
    }

    @Test
    void rejectsDataTablesWithLineInformation() throws IOException {
        Path feature = feature("PK_022_API_TEST.feature", """
                Feature: Table
                  Scenario: Table step
                    Given I have data
                      | key | value |
                      | id  | 25    |
                """);

        RuntimeException error = assertThrows(RuntimeException.class, () -> generator.generate(feature, false));

        assertTrue(error.getMessage().contains("Data Table"));
        assertTrue(error.getMessage().contains(":4:"));
    }

    @Test
    void rejectsDocStringsWithLineInformation() throws IOException {
        Path feature = feature("PK_023_API_TEST.feature", """
                Feature: Doc string
                  Scenario: Doc step
                    Given I have data
                      \"\"\"
                      { \"id\": 25 }
                      \"\"\"
                """);

        RuntimeException error = assertThrows(RuntimeException.class, () -> generator.generate(feature, false));

        assertTrue(error.getMessage().contains("Doc String"));
    }

    @Test
    void rejectsRulesWithLineInformation() throws IOException {
        Path feature = feature("PK_024_API_TEST.feature", """
                Feature: Rule
                  Rule: Grouped behavior
                    Scenario: Verify
                      Given I start
                """);

        RuntimeException error = assertThrows(RuntimeException.class, () -> generator.generate(feature, false));

        assertTrue(error.getMessage().contains("Unsupported Rule"));
        assertTrue(error.getMessage().contains(":2:"));
    }

    @Test
    void protectsExistingTestDefinitionUnlessForceIsEnabled() throws IOException {
        Path feature = feature("PK_025_API_TEST.feature", oneScenario("Given I start"));
        Path output = projectRoot.resolve("src/test/java/tests/pk025/PK_025_API_TEST.java");
        Files.createDirectories(output.getParent());
        Files.writeString(output, "manual BABELL binding");

        RuntimeException error = assertThrows(RuntimeException.class, () -> generator.generate(feature, false));

        assertTrue(error.getMessage().contains("already exists"));
        assertTrue(error.getMessage().contains("--force"));
        assertEquals("manual BABELL binding", Files.readString(output));
    }

    @Test
    void forceRegenerationReplacesExistingTestDefinition() throws IOException {
        Path feature = feature("PK_026_API_TEST.feature", oneScenario("Given I start"));
        Path output = projectRoot.resolve("src/test/java/tests/pk026/PK_026_API_TEST.java");
        Files.createDirectories(output.getParent());
        Files.writeString(output, "manual BABELL binding");

        generator.generate(feature, true);

        assertTrue(Files.readString(output).contains("public class PK_026_API_TEST"));
        assertFalse(Files.readString(output).contains("manual BABELL binding"));
    }

    @Test
    void recursivelyProcessesDirectoryAndKeepsIndependentFailures() throws IOException {
        feature("nested/PK_027_API_TEST.feature", oneScenario("Given I start"));
        feature("nested/bad.feature", oneScenario("Given I start"));

        List<GherkinToFunctions.GenerationOutcome> outcomes = generator.generateDirectory(featureDirectory, false);

        assertEquals(2, outcomes.size());
        assertTrue(outcomes.getFirst().result().isPresent());
        assertTrue(outcomes.getLast().error().orElseThrow().contains("PK_NNN_API_TEST.feature"));
        assertTrue(Files.exists(projectRoot.resolve("src/test/java/tests/pk027/PK_027_API_TEST.java")));
    }

    @Test
    void rejectsUnknownCliOptionsWithUsage() {
        assertEquals(1, generator.run(new String[]{"--bad", "file.feature"}));
    }

    private Path feature(String relativePath, String content) throws IOException {
        Path file = featureDirectory.resolve(relativePath);
        Files.createDirectories(file.getParent());
        return Files.writeString(file, content.stripLeading());
    }

    private String generate(Path feature) throws IOException {
        return Files.readString(generator.generate(feature, false).outputPath());
    }

    private String oneScenario(String step) {
        return "Feature: Example\n  Scenario: Example scenario\n    " + step.strip().replace("\n", "\n    ") + "\n";
    }

    private int occurrences(String text, String needle) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
