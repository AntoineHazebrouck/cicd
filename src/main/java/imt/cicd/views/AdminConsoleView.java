package imt.cicd.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import imt.cicd.data.BuildHistory;
import imt.cicd.data.BuildHistory.BuildRecap;
import imt.cicd.data.FullPipeline;
import jakarta.annotation.security.RolesAllowed;

@Route("admin-console")
@PageTitle("Admin console")
@RolesAllowed("ADMIN")
public class AdminConsoleView extends Composite<VerticalLayout> {

    private final Grid<BuildRecap> grid = new Grid<>(BuildRecap.class);

    @Override
    protected VerticalLayout initContent() {
        refreshUi();

        var col = new VerticalLayout();

        var subcol = new VerticalLayout();
        subcol.add(grid);

        var row1 = new HorizontalLayout(
            new Button("deploy 'archi-project'", event -> {
                var example =
                    "https://github.com/AntoineHazebrouckOrg/archi-project";

                FullPipeline.run(example);

                refreshUi();
            })
        );

        col.add(new H1("Admin console"), row1, grid);
        return col;
    }

    private void refreshUi() {
        grid.setItems(BuildHistory.history());
    }
}
