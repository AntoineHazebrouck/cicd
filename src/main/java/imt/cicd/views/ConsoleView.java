package imt.cicd.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route
@PageTitle("Console")
public class ConsoleView extends VerticalLayout {

    public ConsoleView() {
        H2 h = new H2("Home");
        Span p = new Span("Welcome to the Home page.");
        add(h, p);
    }
}
