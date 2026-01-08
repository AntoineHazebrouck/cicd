package imt.cicd.data.orchestration;

public interface StepCallback {
    void update(int stepIndex, boolean success);
}