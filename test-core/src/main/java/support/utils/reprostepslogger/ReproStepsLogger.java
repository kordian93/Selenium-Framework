package support.utils.reprostepslogger;

import lombok.Getter;

public class ReproStepsLogger {

    @Getter
    private ReproStepsStage stage = ReproStepsStage.NOT_STARTED;

    private final StringBuilder setupSteps = new StringBuilder();
    private final StringBuilder testSteps = new StringBuilder();
    private final StringBuilder teardownSteps = new StringBuilder();

    private int stepCount;

    public void addStep(String step) {
        switch (stage) {
            case SETUP -> addStepToSetup(step);
            case TEST -> addStepToTest(step);
            case TEARDOWN -> addStepToTeardown(step);
        }
    }

    public void setStage(ReproStepsStage stage) {
        this.stage = stage;
        stepCount = 1;
        switch (stage) {
            case SETUP -> setupSteps.append(stage.getText()).append("\n");
            case TEST -> testSteps.append(stage.getText()).append("\n");
            case TEARDOWN -> teardownSteps.append(stage.getText()).append("\n");
        }
    }

    public String getSteps() {
        clearIfNoStepsRecorded(setupSteps);
        clearIfNoStepsRecorded(testSteps);
        clearIfNoStepsRecorded(teardownSteps);

        if (setupSteps.isEmpty() && testSteps.isEmpty() && teardownSteps.isEmpty()) {
            return "No steps recorded.";
        }

        return setupSteps + "\n" +
                testSteps + "\n" +
                teardownSteps;
    }

    private void addStepToSetup(String step) {
        setupSteps.append(stepCount++).append(". ").append(step).append("\n");
    }

    private void addStepToTest(String step) {
        testSteps.append(stepCount++).append(". ").append(step).append("\n");
    }

    private void addStepToTeardown(String step) {
        teardownSteps.append(stepCount++).append(". ").append(step).append("\n");
    }

    private void clearIfNoStepsRecorded(StringBuilder steps) {
        // If there is no step 1 then delete header
        if (!steps.toString().contains("1.")) {
            steps.delete(0, steps.length());
        }
    }
}
