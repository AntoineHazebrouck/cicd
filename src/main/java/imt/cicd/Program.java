package imt.cicd;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@StyleSheet(Lumo.STYLESHEET)
@SpringBootApplication
class Program {

    public static void main(String[] args) {
        SpringApplication.run(Program.class, args);
    }
}
