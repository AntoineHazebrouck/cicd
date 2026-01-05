package imt.cicd.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("")
@PageTitle("Console")
@PermitAll
public class ConsoleView extends Composite<VerticalLayout> {

    @Override
    protected VerticalLayout initContent() {
        return new VerticalLayout(new H1("qsdq"));
    }
}
