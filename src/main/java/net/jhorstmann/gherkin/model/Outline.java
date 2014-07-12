package net.jhorstmann.gherkin.model;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Outline extends StepContainer {
    private final List<Row> examples = new ArrayList<>();

    public Outline(Feature feature, int lineNumber, String keyword, String description) {
        super(feature, lineNumber, keyword, description);
    }

    public List<Row> getExamples() {
        return examples;
    }

    private static String replaceExampleValues(String string, Pattern matchPattern, Map<String, String> exampleValues) {
        StringBuffer sb = new StringBuffer(string.length());
        Matcher matcher = matchPattern.matcher(string);
        while (matcher.find()) {
            String var = matcher.group(1);
            String value = exampleValues.get(var);
            matcher.appendReplacement(sb, Pattern.quote(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private Row buildRow(Pattern pattern, Row exampleStepData, Map<String, String> exampleValues) {
        Row row = new Row(getFeature(), exampleStepData.getLineNumber());
        row.getComments().addAll(exampleStepData.getComments());
        for (String cell : exampleStepData.getCells()) {
            row.getCells().add(replaceExampleValues(cell, pattern, exampleValues));
        }
        return row;
    }

    private Scenario buildScenario(Pattern pattern, Row exampleRow, Map<String, String> exampleValues) {
        Scenario scenario = new Scenario(getFeature(), exampleRow.getLineNumber(), getKeyword(), getDescription() + " " + exampleValues);
        for (Step exampleStep : getSteps()) {
            String stepDescription = replaceExampleValues(exampleStep.getDescription(), pattern, exampleValues);
            Step step = new Step(scenario, exampleStep.getLineNumber(), exampleStep.getKeyword(), stepDescription);
            step.getComments().addAll(exampleStep.getComments());
            step.getDocs().addAll(exampleStep.getDocs());

            for (Row exampleStepData : exampleStep.getRows()) {
                step.getRows().add(buildRow(pattern, exampleStepData, exampleValues));
            }

            scenario.getSteps().add(step);
        }
        return scenario;
    }

    public List<Scenario> buildScenarios() {
        List<Scenario> result = new ArrayList<>(examples.size()-1);
        List<String> header = examples.get(0).getCells();
        Pattern pattern = Pattern.compile("<(" + Joiner.on("|").join(header) + ")>");
        for (Row exampleRow : examples.subList(1, examples.size())) {
            Map<String, String> exampleValues = Table.rowToMap(header, exampleRow);
            Scenario scenario = buildScenario(pattern, exampleRow, exampleValues);

            result.add(scenario);
        }
        return result;
    }
}
