package imt.cicd.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        Anchor githubLogin = new Anchor(
            "/oauth2/authorization/github",
            "Sign in with GitHub"
        );
        githubLogin.getElement().setAttribute("router-ignore", true);
        add(githubLogin);
    }
}
