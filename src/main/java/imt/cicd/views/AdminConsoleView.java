package imt.cicd.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import imt.cicd.data.FullPipeline;
import imt.cicd.views.components.PipelinesGrid;
import jakarta.annotation.security.RolesAllowed;

@Route("admin-console")
@PageTitle("Admin console")
@RolesAllowed("ADMIN")
public class AdminConsoleView extends Composite<VerticalLayout> {

    private final PipelinesGrid pipelines = new PipelinesGrid();

    @Override
    protected VerticalLayout initContent() {
        return new VerticalLayout(
            new H1("Admin console"),
            new Button("Run pipeline for 'archi-project'", event -> {
                var example =
                    "https://github.com/AntoineHazebrouckOrg/archi-project";

                FullPipeline.run(example);
                pipelines.refresh();
            }),
            new Button("Refresh", event -> {
                pipelines.refresh();
            }),
            pipelines
        );
    }
}
