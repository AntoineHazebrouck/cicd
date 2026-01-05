package imt.cicd.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import imt.cicd.data.BuildDockerImage;
import imt.cicd.data.BuildHistory;
import imt.cicd.data.BuildHistory.BuildRecap;
import imt.cicd.data.CloneRepository;
import jakarta.annotation.security.RolesAllowed;
import java.time.LocalDateTime;

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
                fullBuild();
            })
        );

        col.add(new H1("Admin console"), row1, grid);
        return col;
    }

    private void fullBuild() {
        var example = "https://github.com/AntoineHazebrouckOrg/archi-project";

        var cloneResult = CloneRepository.run(example);
        if (cloneResult.isStatus()) Notification.show("Cloned " + example);
        else Notification.show("Failed to clone " + example);

        var buildResult = BuildDockerImage.run(cloneResult.getFolder());
        if (buildResult.isStatus()) Notification.show("Built " + example);
        else Notification.show("Failed to build " + example);

        BuildHistory.add(
            BuildRecap.builder()
                .status(cloneResult.isStatus() && buildResult.isStatus())
                .image(buildResult.getImage())
                .imageTag(buildResult.getImageTag())
                .time(LocalDateTime.now())
                .build()
        );

        refreshUi();
    }

    private void refreshUi() {
        grid.setItems(BuildHistory.history());
    }
}
