package imt.cicd;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import imt.cicd.views.AdminConsoleView;
import imt.cicd.views.ConsoleView;
import jakarta.annotation.security.PermitAll;

@Layout
@PermitAll
public class MainLayout extends AppLayout {

    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("CI/CD for AntoineHazebrouckOrg");
        title.getStyle().set("margin", "0");
        addToNavbar(toggle, title);

        var nav = new SideNav();
        nav.addItem(new SideNavItem("Console", ConsoleView.class));
        nav.addItem(new SideNavItem("Admin console", AdminConsoleView.class));

        addToDrawer(nav);
        setDrawerOpened(false);
    }
}
