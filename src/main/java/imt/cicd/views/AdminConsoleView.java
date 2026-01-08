package imt.cicd.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import imt.cicd.data.FullPipeline;
import imt.cicd.views.components.PipelineStepper;
import imt.cicd.views.components.PipelinesGrid;
import jakarta.annotation.security.RolesAllowed;

@Route("admin-console")
@PageTitle("Admin console")
@RolesAllowed("ADMIN")
public class AdminConsoleView extends Composite<VerticalLayout> {

    private final PipelinesGrid pipelines = new PipelinesGrid();
    private final PipelineStepper stepper = new PipelineStepper();

    @Override
    protected VerticalLayout initContent() {
        return new VerticalLayout(
            new H1("Admin console"),
                stepper,
            new Button("Run pipeline for 'archi-project'", event -> {
                var example =
                    "https://github.com/AntoineHazebrouckOrg/archi-project";
                UI ui = UI.getCurrent();
                stepper.reset();

                new Thread(() -> {
                    FullPipeline.run(example,ui, (step, success) -> {
                        ui.access(() -> {
                            stepper.update(step, success);
                            pipelines.refresh();
                        });
                    });
                }).start();
            }),
            new Button("Refresh", event -> {
                pipelines.refresh();
            }),
            pipelines
        );
    }
}
