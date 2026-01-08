package imt.cicd.data.orchestration;

import imt.cicd.data.HasStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChainedOrchestrator {

    private final StepsHandler stepsHandler = new StepsHandler();

    public static class StepsHandler {

        private final List<HasStatus> previousSteps = new ArrayList<>();
        private boolean stop = false;

        public boolean add(HasStatus step) {
            return previousSteps.add(step);
        }

        public List<HasStatus> getAllSteps() {
            return previousSteps;
        }

        public HasStatus getLast() {
            return previousSteps.getLast();
        }

        public <T extends HasStatus> T find(Class<T> clazz) {
            return findOptional(clazz).orElseThrow(() ->
                new IllegalStateException(
                    "Result class " +
                    clazz.getSimpleName() +
                    " was not found, check the executed steps"
                )
            );
        }

        public <T extends HasStatus> Optional<T> findOptional(Class<T> clazz) {
            return previousSteps
                .stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst();
        }

        public String formattedStepStatus(
            Class<? extends HasStatus> clazz,
            String stepName
        ) {
            var found = findOptional(clazz);

            if (found.isPresent()) {
                if (found.get().getStatus()) return stepName + "_OK";
                return stepName + "_FAILED";
            } else return stepName + "_SKIPPED";
        }

        public void stopThere() {
            stop = true;
        }

        public void keepGoing() {
            stop = false;
        }

        public boolean isStopped() {
            return stop;
        }
    }

    private ChainedOrchestrator(StepCallback stepCallback) {
        this.stepCallback = stepCallback;
    }

    public static ChainedOrchestrator withStepCompletionCallback(
        StepCallback stepCallback
    ) {
        return new ChainedOrchestrator(stepCallback);
    }

    private int stepIndex = 0;
    private final StepCallback stepCallback;

    public ChainedOrchestrator step(
        Supplier<HasStatus> stepCode,
        Consumer<StepsHandler> onFailure
    ) {
        return step(steps -> stepCode.get(), onFailure);
    }

    public ChainedOrchestrator step(
        Function<StepsHandler, HasStatus> stepCode,
        Consumer<StepsHandler> onFailure
    ) {
        if (stepsHandler.isStopped()) return this;

        var step = stepCode.apply(stepsHandler);

        log.info(
            "Calling callback with index {} and status {}",
            stepIndex,
            step.getStatus()
        );
        stepCallback.update(stepIndex, step.getStatus());
        stepIndex++;

        if (step.getStatus() == false) onFailure.accept(stepsHandler);

        stepsHandler.add(step);
        return this;
    }

    public StepsHandler finish() {
        return stepsHandler;
    }
}
