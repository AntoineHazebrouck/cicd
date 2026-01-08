package imt.cicd.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import imt.cicd.config.StaticIoc;
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
            new HorizontalLayout(
                new Button("Run pipeline for 'archi-project'", event -> {
                    Notification.show("Starting pipeline");

                    stepper.reset();

                    UI ui = UI.getCurrent();

                    StaticIoc.getBean(AsyncWrapper.class)
                        .pipeline(ui, stepper)
                        .thenAccept(builds -> {
                            ui.access(() -> {
                                pipelines.refresh();
                            });
                        });
                }),
                new Button("Refresh", event -> {
                    pipelines.refresh();
                }),
                stepper
            ),
            pipelines
        );
    }
}
