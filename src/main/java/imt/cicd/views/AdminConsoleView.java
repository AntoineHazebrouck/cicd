package imt.cicd.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import imt.cicd.data.BuildDockerImage;
import imt.cicd.data.CloneRepository;
import jakarta.annotation.security.RolesAllowed;

@Route("admin-console")
@PageTitle("Admin console")
@RolesAllowed("ADMIN")
public class AdminConsoleView extends Composite<VerticalLayout> {

    @Override
    protected VerticalLayout initContent() {
        var col = new VerticalLayout();

        var row1 = new HorizontalLayout(
            new Button("deploy 'archi-project'", event -> {
                var example =
                    "https://github.com/AntoineHazebrouckOrg/archi-project.git";
                var cloneResult = CloneRepository.run(example);

                if (cloneResult.getStatus()) Notification.show(
                    "Cloned " + example
                );
                else Notification.show("Failed to clone " + example);

                var buildResult = BuildDockerImage.run(cloneResult.getFolder());

                if (buildResult) Notification.show("Built " + example);
                else Notification.show("Failed to build " + example);
            })
        );

        col.add(new H1("Hello world"), row1);
        return col;
    }
}
