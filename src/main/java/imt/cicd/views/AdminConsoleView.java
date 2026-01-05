package imt.cicd.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
                var ok = CloneRepository.run(
                    "https://github.com/AntoineHazebrouck/AntoineHazebrouck.git"
                );

                if (ok) Notification.show(
                    "Cloned " +
                    "https://github.com/AntoineHazebrouck/AntoineHazebrouck.git"
                );
                else Notification.show(
                    "Failed to clone " +
                    "https://github.com/AntoineHazebrouck/AntoineHazebrouck.git"
                );
            })
        );

        col.add(new H1("Hello world"), row1);
        return col;
    }
}
