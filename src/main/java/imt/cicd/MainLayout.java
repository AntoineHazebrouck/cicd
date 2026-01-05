package imt.cicd;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import imt.cicd.views.AdminConsoleView;
import imt.cicd.views.ConsoleView;
import jakarta.annotation.security.PermitAll;

@Layout
@PermitAll
public class MainLayout extends AppLayout {

    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("CI/CD");
        title.getStyle().set("margin", "0");
        addToNavbar(toggle, title);

        RouterLink about = new RouterLink(
            "Admin console",
            AdminConsoleView.class
        );
        RouterLink home = new RouterLink("Console", ConsoleView.class);

        VerticalLayout drawer = new VerticalLayout(home, about);
        drawer.setPadding(false);
        drawer.setSpacing(false);
        addToDrawer(drawer);
    }
}
