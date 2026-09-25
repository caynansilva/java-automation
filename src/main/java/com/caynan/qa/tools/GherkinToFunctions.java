package com.caynan.qa.tools;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.ParseError;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Step;
import io.cucumber.messages.types.StepKeywordType;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class GherkinToFunctions {
    private static final Pattern FEATURE_NAME = Pattern.compile("(?i)^PK_(\\d{3})_API_TEST\\.feature$");
    private static final Pattern PARAMETER = Pattern.compile(
            "\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'|(?<![\\w.])-?\\d+\\.\\d+(?![\\w.])|(?<![\\w.])-?\\d+(?![\\w.])"
    );
    private static final Pattern WORD = Pattern.compile("[\\p{IsAlphabetic}\\p{IsDigit}]+");
    private static final Set<String> JAVA_KEYWORDS = Set.of(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
            "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
            "volatile", "while", "true", "false", "null", "record", "sealed", "permits", "var", "yield"
    );

    private final Path projectRoot;

    public GherkinToFunctions(Path projectRoot) {
        this.projectRoot = projectRoot.toAbsolutePath().normalize();
    }

    public static void main(String[] args) {
        int exitCode = new GherkinToFunctions(findProjectRoot()).run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    public int run(String[] args) {
        try {
            CliOptions options = parseArguments(args);
            if (options.directoryMode()) {
                return runDirectory(options.input(), options.force());
            }
            GenerationResult result = generate(options.input(), options.force());
            printSuccess(result);
            return 0;
        } catch (G2fException exception) {
            System.err.println("G2F ERROR:");
            System.err.println(exception.getMessage());
            return 1;
        }
    }

    public GenerationResult generate(Path input, boolean force) {
        Path featurePath = resolveFeaturePath(input);
        FeatureIdentity identity = identifyFeature(featurePath);
        Path outputPath = outputPath(identity);
        FeatureBindings feature = readBindings(featurePath);
        String source = renderJava(identity, featurePath, feature.bindings());
        writeSource(outputPath, source, force);
        return new GenerationResult(featurePath, outputPath, feature.bindings().size(), feature.counts());
    }

    public List<GenerationOutcome> generateDirectory(Path directory, boolean force) {
        Path directoryPath = directory.isAbsolute() ? directory.normalize() : projectRoot.resolve(directory).normalize();
        if (!Files.isDirectory(directoryPath)) {
            throw new G2fException("Directory does not exist: " + displayPath(directoryPath));
        }

        List<Path> features;
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            features = paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".feature"))
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        } catch (IOException exception) {
            throw new G2fException("Could not scan directory " + displayPath(directoryPath) + ": " + exception.getMessage());
        }
        if (features.isEmpty()) {
            throw new G2fException("No .feature files found under " + displayPath(directoryPath));
        }

        List<GenerationOutcome> outcomes = new ArrayList<>();
        for (Path feature : features) {
            try {
                outcomes.add(GenerationOutcome.success(generate(feature, force)));
            } catch (G2fException exception) {
                outcomes.add(GenerationOutcome.failure(feature, exception.getMessage()));
            }
        }
        return outcomes;
    }

    private int runDirectory(Path directory, boolean force) {
        List<GenerationOutcome> outcomes = generateDirectory(directory, force);
        for (GenerationOutcome outcome : outcomes) {
            if (outcome.result().isPresent()) {
                printSuccess(outcome.result().orElseThrow());
            } else {
                System.err.println("G2F ERROR: " + displayPath(outcome.input()) + " — " + outcome.error().orElseThrow());
            }
        }
        long successes = outcomes.stream().filter(outcome -> outcome.result().isPresent()).count();
        long failures = outcomes.size() - successes;
        System.out.printf("Directory summary: %d succeeded, %d failed.%n", successes, failures);
        return failures == 0 ? 0 : 1;
    }

    private CliOptions parseArguments(String[] args) {
        boolean force = false;
        boolean directoryMode = false;
        Path input = null;
        for (int index = 0; index < args.length; index++) {
            String argument = args[index];
            if (argument.equals("--force")) {
                force = true;
            } else if (argument.equals("--dir")) {
                if (directoryMode) {
                    throw usageError("--dir may be specified only once.");
                }
                if (input != null) {
                    throw usageError("Do not combine a feature path with --dir.");
                }
                directoryMode = true;
                if (++index >= args.length) {
                    throw usageError("--dir requires a directory path.");
                }
                input = Path.of(args[index]);
            } else if (argument.startsWith("--")) {
                throw usageError("Unknown option: " + argument);
            } else if (input != null) {
                throw usageError("Provide one feature path, or one directory with --dir.");
            } else {
                input = Path.of(argument);
            }
        }
        if (input == null) {
            throw usageError("Provide a feature path or --dir <directory>.");
        }
        return new CliOptions(input, force, directoryMode);
    }

    private G2fException usageError(String detail) {
        return new G2fException(detail + System.lineSeparator()
                + "Usage: g2f [--force] <feature> | g2f [--force] --dir <directory>");
    }

    private Path resolveFeaturePath(Path input) {
        Path path = input.isAbsolute() ? input.normalize() : projectRoot.resolve(input).normalize();
        Path featureRoot = projectRoot.resolve("src/test/resources/features").normalize();
        if (!path.startsWith(featureRoot)) {
            throw new G2fException("Feature must be inside " + displayPath(featureRoot) + ".");
        }
        if (!Files.isRegularFile(path)) {
            throw new G2fException("Feature file does not exist: " + displayPath(path));
        }
        return path;
    }

    private FeatureIdentity identifyFeature(Path featurePath) {
        String fileName = featurePath.getFileName().toString();
        Matcher matcher = FEATURE_NAME.matcher(fileName);
        if (!matcher.matches()) {
            throw new G2fException(fileName + " does not match PK_NNN_API_TEST.feature. Rename the feature using that convention.");
        }
        String number = matcher.group(1);
        String className = "PK_" + number + "_API_TEST";
        return new FeatureIdentity(className, "tests.pk" + number.toLowerCase(Locale.ROOT));
    }

    private Path outputPath(FeatureIdentity identity) {
        return projectRoot.resolve("src/test/java")
                .resolve(identity.packageName().replace('.', '/'))
                .resolve(identity.className() + ".java");
    }

    private FeatureBindings readBindings(Path featurePath) {
        List<Envelope> envelopes;
        try {
            envelopes = GherkinParser.builder()
                    .includeSource(false)
                    .includeGherkinDocument(true)
                    .includePickles(false)
                    .build()
                    .parse(featurePath)
                    .toList();
        } catch (IOException | RuntimeException exception) {
            throw new G2fException(displayPath(featurePath) + ": Could not parse Gherkin: " + exception.getMessage());
        }

        for (Envelope envelope : envelopes) {
            Optional<ParseError> parseError = envelope.getParseError();
            if (parseError.isPresent()) {
                ParseError error = parseError.orElseThrow();
                throw new G2fException(displayPath(featurePath) + lineSuffix(error.getSource().getLocation().map(location -> location.getLine()).orElse(null))
                        + ": " + error.getMessage());
            }
        }
        GherkinDocument document = envelopes.stream()
                .flatMap(envelope -> envelope.getGherkinDocument().stream())
                .findFirst()
                .orElseThrow(() -> new G2fException(displayPath(featurePath) + ": No Feature was found."));
        Feature feature = document.getFeature()
                .orElseThrow(() -> new G2fException(displayPath(featurePath) + ": No Feature was found."));
        return collectFeatureBindings(featurePath, feature);
    }

    private FeatureBindings collectFeatureBindings(Path featurePath, Feature feature) {
        List<Background> backgrounds = new ArrayList<>();
        List<Scenario> scenarios = new ArrayList<>();
        feature.getChildren().forEach(child -> collectFeatureChild(featurePath, child, backgrounds, scenarios));
        if (scenarios.isEmpty()) {
            throw new G2fException(displayPath(featurePath) + ": No Scenario was found. Add one Scenario to this Feature.");
        }
        if (scenarios.size() > 1) {
            throw new G2fException(displayPath(featurePath) + " contains " + scenarios.size() + " scenarios."
                    + System.lineSeparator() + "BABELL requires one independently managed scenario per Feature."
                    + System.lineSeparator() + "Split the scenarios into separate Feature files before running G2F.");
        }

        LinkedHashMap<String, Binding> bindings = new LinkedHashMap<>();
        for (Background background : backgrounds) {
            addSteps(featurePath, background.getSteps(), bindings);
        }
        Scenario scenario = scenarios.getFirst();
        if (!scenario.getExamples().isEmpty() || scenario.getKeyword().toLowerCase(Locale.ROOT).contains("outline")) {
            throw unsupported(featurePath, "Scenario Outline / Examples", scenario.getLocation().getLine(),
                    "Replace it with a single Scenario before running G2F.");
        }
        addSteps(featurePath, scenario.getSteps(), bindings);
        return new FeatureBindings(List.copyOf(bindings.values()), countParameters(bindings.values()));
    }

    private void collectFeatureChild(Path featurePath, FeatureChild child, List<Background> backgrounds, List<Scenario> scenarios) {
        if (child.getRule().isPresent()) {
            var rule = child.getRule().orElseThrow();
            throw unsupported(featurePath, "Rule", rule.getLocation().getLine(),
                    "Move its scenario into a separate Feature supported by BABELL G2F.");
        }
        child.getBackground().ifPresent(backgrounds::add);
        child.getScenario().ifPresent(scenarios::add);
    }

    private void addSteps(Path featurePath, List<Step> steps, Map<String, Binding> bindings) {
        StepKeywordType previous = null;
        for (Step step : steps) {
            if (step.getDataTable().isPresent()) {
                throw unsupported(featurePath, "Data Table", step.getDataTable().orElseThrow().getLocation().getLine(),
                        "Rewrite the step without a table before running G2F.");
            }
            if (step.getDocString().isPresent()) {
                throw unsupported(featurePath, "Doc String", step.getDocString().orElseThrow().getLocation().getLine(),
                        "Rewrite the step without a doc string before running G2F.");
            }
            StepKeywordType keywordType = step.getKeywordType().orElse(StepKeywordType.UNKNOWN);
            StepKeywordType effectiveType = effectiveKeyword(featurePath, step, keywordType, previous);
            if (keywordType != StepKeywordType.CONJUNCTION) {
                previous = effectiveType;
            }
            Binding binding = createBinding(step.getText(), effectiveType);
            bindings.putIfAbsent(binding.annotation() + "\0" + binding.expression(), binding);
        }
    }

    private StepKeywordType effectiveKeyword(Path featurePath, Step step, StepKeywordType type, StepKeywordType previous) {
        if (type == StepKeywordType.CONTEXT || type == StepKeywordType.ACTION || type == StepKeywordType.OUTCOME) {
            return type;
        }
        if (type == StepKeywordType.CONJUNCTION && previous != null) {
            return previous;
        }
        throw unsupported(featurePath, "step keyword " + step.getKeyword().trim(), step.getLocation().getLine(),
                "Start each step group with Given, When, or Then before using And or But.");
    }

    private Binding createBinding(String text, StepKeywordType keywordType) {
        Matcher matcher = PARAMETER.matcher(text);
        StringBuilder expression = new StringBuilder();
        List<Parameter> parameters = new ArrayList<>();
        int cursor = 0;
        while (matcher.find()) {
            expression.append(escapeCucumberLiteral(text.substring(cursor, matcher.start())));
            String literal = matcher.group();
            ParameterKind kind = parameterKind(literal);
            String parameterName = parameterName(text.substring(0, matcher.start()), kind,
                    parameters.stream().map(Parameter::name).collect(java.util.stream.Collectors.toSet()));
            parameters.add(new Parameter(kind, parameterName));
            expression.append(kind.expression());
            cursor = matcher.end();
        }
        expression.append(escapeCucumberLiteral(text.substring(cursor)));
        String methodName = methodName(text);
        String annotation = annotation(keywordType);
        return new Binding(annotation, expression.toString(), methodName, List.copyOf(parameters));
    }

    private ParameterKind parameterKind(String literal) {
        if (literal.startsWith("\"") || literal.startsWith("'")) {
            return ParameterKind.STRING;
        }
        return literal.contains(".") ? ParameterKind.DOUBLE : ParameterKind.INTEGER;
    }

    private String parameterName(String precedingText, ParameterKind kind, Set<String> usedNames) {
        List<String> words = words(precedingText.toLowerCase(Locale.ROOT));
        String suggested = switch (kind) {
            case STRING -> words.stream()
                    .filter(Set.of("name", "pokemon", "type", "ability")::contains)
                    .reduce((first, second) -> second)
                    .orElse("value");
            case INTEGER -> precedingText.toLowerCase(Locale.ROOT).matches(".*status\\s+code.*") ? "statusCode"
                    : words.contains("id") ? "id" : "number";
            case DOUBLE -> "decimalValue";
        };
        String uniqueName = suggested;
        int suffix = 2;
        while (usedNames.contains(uniqueName)) {
            uniqueName = suggested + suffix++;
        }
        return uniqueName;
    }

    private String methodName(String text) {
        String withoutLiterals = PARAMETER.matcher(text).replaceAll(" ");
        List<String> words = words(withoutLiterals);
        if (!words.isEmpty() && Set.of("the", "a", "an").contains(words.getFirst().toLowerCase(Locale.ROOT))) {
            words = words.subList(1, words.size());
        }
        StringBuilder name = new StringBuilder();
        for (int index = 0; index < words.size(); index++) {
            String word = words.get(index).toLowerCase(Locale.ROOT);
            if (index == 0) {
                name.append(word);
            } else {
                name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
        }
        if (name.isEmpty()) {
            name.append("step");
        }
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            name.insert(0, "step");
        }
        if (JAVA_KEYWORDS.contains(name.toString())) {
            name.append("Step");
        }
        return name.toString();
    }

    private List<String> words(String text) {
        List<String> values = new ArrayList<>();
        Matcher matcher = WORD.matcher(text);
        while (matcher.find()) {
            values.add(matcher.group());
        }
        return values;
    }

    private String annotation(StepKeywordType type) {
        return switch (type) {
            case CONTEXT -> "Given";
            case ACTION -> "When";
            case OUTCOME -> "Then";
            default -> throw new IllegalArgumentException("Unsupported step keyword type: " + type);
        };
    }

    private String escapeCucumberLiteral(String literal) {
        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < literal.length(); index++) {
            char character = literal.charAt(index);
            if ("{}()/!\\".indexOf(character) >= 0) {
                escaped.append('\\');
            }
            escaped.append(character);
        }
        return escaped.toString();
    }

    private String renderJava(FeatureIdentity identity, Path featurePath, List<Binding> bindings) {
        String featureResource = projectRoot.resolve("src/test/resources").relativize(featurePath).toString().replace('\\', '/');
        StringBuilder source = new StringBuilder();
        source.append("package ").append(identity.packageName()).append(";\n\n");
        List.of("Given", "When", "Then").stream()
                .filter(annotation -> bindings.stream().anyMatch(binding -> binding.annotation().equals(annotation)))
                .forEach(annotation -> source.append("import io.cucumber.java.en.").append(annotation).append(";\n"));
        source.append("import org.junit.platform.suite.api.ConfigurationParameter;\n")
                .append("import org.junit.platform.suite.api.IncludeEngines;\n")
                .append("import org.junit.platform.suite.api.SelectClasspathResource;\n")
                .append("import org.junit.platform.suite.api.Suite;\n\n")
                .append("import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;\n")
                .append("import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;\n\n")
                .append("@Suite\n")
                .append("@IncludeEngines(\"cucumber\")\n")
                .append("@SelectClasspathResource(\"").append(escapeJava(featureResource)).append("\")\n")
                .append("@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = \"").append(identity.packageName()).append("\")\n")
                .append("@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = \"pretty\")\n")
                .append("public class ").append(identity.className()).append(" {\n");
        Set<String> usedNames = new HashSet<>();
        for (Binding binding : bindings) {
            String method = uniqueMethodName(binding.methodName(), usedNames);
            appendBinding(source, binding, method);
        }
        return source.append("}\n").toString();
    }

    private String uniqueMethodName(String baseName, Set<String> usedNames) {
        String candidate = baseName;
        int suffix = 2;
        while (!usedNames.add(candidate)) {
            candidate = baseName + suffix++;
        }
        return candidate;
    }

    private void appendBinding(StringBuilder source, Binding binding, String methodName) {
        source.append("\n    @").append(binding.annotation()).append("(\"")
                .append(escapeJava(binding.expression())).append("\")\n")
                .append("    public void ").append(methodName).append('(');
        for (int index = 0; index < binding.parameters().size(); index++) {
            if (index > 0) {
                source.append(", ");
            }
            Parameter parameter = binding.parameters().get(index);
            source.append(parameter.kind().javaType()).append(' ').append(parameter.name());
        }
        source.append(") {\n")
                .append("        throw new UnsupportedOperationException(\"TODO: delegate this binding to reusable BABELL behavior\");\n")
                .append("    }\n");
    }

    private String escapeJava(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private Map<ParameterKind, Integer> countParameters(Iterable<Binding> bindings) {
        Map<ParameterKind, Integer> counts = new LinkedHashMap<>();
        for (ParameterKind kind : ParameterKind.values()) {
            counts.put(kind, 0);
        }
        for (Binding binding : bindings) {
            for (Parameter parameter : binding.parameters()) {
                counts.compute(parameter.kind(), (ignored, count) -> count + 1);
            }
        }
        return counts;
    }

    private void writeSource(Path outputPath, String source, boolean force) {
        if (Files.exists(outputPath) && !force) {
            throw new G2fException(outputPath.getFileName() + " already exists.\nUse --force only if intentional regeneration is required.");
        }
        Path temporaryFile = null;
        try {
            Files.createDirectories(outputPath.getParent());
            temporaryFile = Files.createTempFile(outputPath.getParent(), outputPath.getFileName().toString(), ".tmp");
            Files.writeString(temporaryFile, source, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            if (force) {
                move(temporaryFile, outputPath, true);
            } else {
                move(temporaryFile, outputPath, false);
            }
        } catch (java.nio.file.FileAlreadyExistsException exception) {
            throw new G2fException(outputPath.getFileName() + " already exists.\nUse --force only if intentional regeneration is required.");
        } catch (IOException exception) {
            throw new G2fException("Could not write " + displayPath(outputPath) + ": " + exception.getMessage());
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // A leftover temporary file does not affect the generated source.
                }
            }
        }
    }

    private void move(Path source, Path destination, boolean replace) throws IOException {
        if (!replace) {
            Files.move(source, destination);
            return;
        }
        try {
            Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private G2fException unsupported(Path featurePath, String construct, Long line, String suggestion) {
        return new G2fException(displayPath(featurePath) + lineSuffix(line) + ": Unsupported " + construct + ". " + suggestion);
    }

    private String lineSuffix(Long line) {
        return line == null ? "" : ":" + line;
    }

    private String displayPath(Path path) {
        if (path.isAbsolute() && path.normalize().startsWith(projectRoot)) {
            return projectRoot.relativize(path.normalize()).toString().replace('\\', '/');
        }
        return path.toString().replace('\\', '/');
    }

    private void printSuccess(GenerationResult result) {
        System.out.println("G2F\n");
        System.out.println("Feature:");
        System.out.println(displayPath(result.featurePath()));
        System.out.println("\nGenerated:");
        System.out.println(displayPath(result.outputPath()));
        System.out.println("\nBindings:");
        System.out.println(result.bindingCount());
        System.out.println("\nParameters:");
        System.out.printf("%d strings%n%d integers%n%d decimals%n",
                result.parameterCounts().get(ParameterKind.STRING),
                result.parameterCounts().get(ParameterKind.INTEGER),
                result.parameterCounts().get(ParameterKind.DOUBLE));
        System.out.println("\nStatus:");
        System.out.println("SUCCESS");
    }

    private static Path findProjectRoot() {
        Path current = Path.of("").toAbsolutePath().normalize();
        for (Path candidate = current; candidate != null; candidate = candidate.getParent()) {
            if (Files.isRegularFile(candidate.resolve("pom.xml"))) {
                return candidate;
            }
        }
        throw new G2fException("Could not locate project root (pom.xml). Run G2F from the BABELL project.");
    }

    private record CliOptions(Path input, boolean force, boolean directoryMode) { }
    private record FeatureIdentity(String className, String packageName) { }
    private record FeatureBindings(List<Binding> bindings, Map<ParameterKind, Integer> counts) { }
    private record Parameter(ParameterKind kind, String name) { }
    private record Binding(String annotation, String expression, String methodName, List<Parameter> parameters) { }

    public record GenerationResult(Path featurePath, Path outputPath, int bindingCount,
                                   Map<ParameterKind, Integer> parameterCounts) { }

    public record GenerationOutcome(Path input, Optional<GenerationResult> result, Optional<String> error) {
        static GenerationOutcome success(GenerationResult result) {
            return new GenerationOutcome(result.featurePath(), Optional.of(result), Optional.empty());
        }

        static GenerationOutcome failure(Path input, String error) {
            return new GenerationOutcome(input, Optional.empty(), Optional.of(error));
        }
    }

    public enum ParameterKind {
        STRING("{string}", "String"), INTEGER("{int}", "int"), DOUBLE("{double}", "double");

        private final String expression;
        private final String javaType;

        ParameterKind(String expression, String javaType) {
            this.expression = expression;
            this.javaType = javaType;
        }

        public String expression() { return expression; }
        public String javaType() { return javaType; }
    }

    private static final class G2fException extends RuntimeException {
        private G2fException(String message) { super(message); }
    }
}
