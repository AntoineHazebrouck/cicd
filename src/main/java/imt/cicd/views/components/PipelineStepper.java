package imt.cicd.views.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class PipelineStepper extends HorizontalLayout {

    private final StepItem clone = new StepItem("Clone");
    private final StepItem sonar = new StepItem("Sonar");
    private final StepItem build = new StepItem("Build");
    private final StepItem deploy = new StepItem("Deploy");
    private final StepItem health = new StepItem("Health Check");
    private final StepItem rollback = new StepItem("RollBack");

    public PipelineStepper() {
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        setSpacing(true);
        add(
            clone,
            arrow(),
            sonar,
            arrow(),
            build,
            arrow(),
            deploy,
            arrow(),
            health,
            arrow(),
            rollback
        );
    }

    private Span arrow() {
        Span s = new Span("➜");
        s.getStyle().set("color", "gray");
        return s;
    }

    public void update(int stepIndex, boolean success) {
        StepItem item =
            switch (stepIndex) {
                case 0 -> clone;
                case 1 -> sonar;
                case 2 -> build;
                case 3 -> deploy;
                case 4 -> health;
                case 5 -> rollback;
                default -> null;
            };
        if (item != null) item.setStatus(success);
    }

    public void reset() {
        clone.reset();
        sonar.reset();
        build.reset();
        deploy.reset();
        health.reset();
        rollback.reset();
    }

    // Classe interne pour un item d'étape unique
    private static class StepItem extends HorizontalLayout {

        private final Icon icon = VaadinIcon.CIRCLE_THIN.create();
        private final Span label;

        public StepItem(String text) {
            this.label = new Span(text);
            setAlignItems(Alignment.CENTER);
            add(icon, label);
        }

        public void setStatus(boolean success) {
            icon
                .getElement()
                .setAttribute(
                    "icon",
                    success ? "vaadin:check-circle" : "vaadin:close-circle"
                );
            icon.setColor(
                success
                    ? "var(--lumo-success-color)"
                    : "var(--lumo-error-color)"
            );
            label.getStyle().set("font-weight", "bold");
        }

        public void reset() {
            icon.getElement().setAttribute("icon", "vaadin:circle-thin");
            icon.setColor("var(--lumo-body-text-color)");
            label.getStyle().set("font-weight", "normal");
        }
    }
}
