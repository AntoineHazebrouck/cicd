package imt.cicd.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import imt.cicd.data.BuildHistory.BuildRecap;
import imt.cicd.data.FullPipeline;
import imt.cicd.views.components.PipelineStepper;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncWrapper {

    @Async
    public CompletableFuture<List<BuildRecap>> pipeline(
        UI ui,
        PipelineStepper stepper
    ) {
        var example = "https://github.com/AntoineHazebrouckOrg/archi-project";
        return CompletableFuture.completedFuture(
            FullPipeline.run(example, (step, success) -> {
                ui.access(() -> {
                    if (success) Notification.show(
                        "Step " + step + " ran successfully"
                    );
                    else Notification.show(
                        "Step " + step + " failed, skipping next steps"
                    );

                    stepper.update(step, success);
                });
            })
        );
    }
}
