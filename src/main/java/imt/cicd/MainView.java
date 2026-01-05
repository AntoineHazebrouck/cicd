package imt.cicd;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route
@PermitAll
public class MainView extends Composite<VerticalLayout> {

    @Override
    protected VerticalLayout initContent() {
        var col = new VerticalLayout();
        col.add(new H1("Hello world"));
        return col;
    }
}
