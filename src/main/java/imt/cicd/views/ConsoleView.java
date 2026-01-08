package imt.cicd.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import imt.cicd.views.components.PipelineStepper;
import imt.cicd.views.components.PipelinesGrid;
import jakarta.annotation.security.PermitAll;

@Route("")
@PageTitle("Console")
@PermitAll
public class ConsoleView extends Composite<VerticalLayout> {

    private final PipelinesGrid pipelines = new PipelinesGrid();
    private final PipelineStepper stepper = new PipelineStepper();

    @Override
    protected VerticalLayout initContent() {
        return new VerticalLayout(
            new H1("Console"),
                stepper,
            new Button("Refresh", event -> {
                pipelines.refresh();
                stepper.reset();
            }),
            pipelines
        );
    }
}
