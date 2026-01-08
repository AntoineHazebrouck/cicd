package imt.cicd.views.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.function.ValueProvider;
import imt.cicd.data.BuildHistory;
import imt.cicd.data.BuildHistory.BuildRecap;
import java.time.format.DateTimeFormatter;

public class PipelinesGrid extends Composite<Grid<BuildRecap>> {

    private final Grid<BuildRecap> grid = new Grid<>(BuildRecap.class, false);

    @Override
    protected Grid<BuildRecap> initContent() {
        var time = grid
            .addColumn(build ->
                build
                    .getTime()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
            )
            .setHeader("Time")
            .setAutoWidth(true)
            .setFlexGrow(0)
            .setFrozen(true)
            .setSortable(true);

        addStandardCol(BuildRecap::getStatus, "Status");
        addStandardCol(BuildRecap::getImageName, "Image name");
        addStandardCol(BuildRecap::getImageTag, "Image tag");
        addStandardCol(BuildRecap::getImageId, "Image id");
        addStandardCol(BuildRecap::getContainerName, "Container name");
        addStandardCol(BuildRecap::getContainerId, "Container id");
        addStandardCol(
            BuildRecap::getRollbackContainerId,
            "Rollback container id"
        );

        addStandardCol(BuildRecap::getSecurity, "Security");
        addStandardCol(BuildRecap::getReliability, "Reliability");
        addStandardCol(BuildRecap::getMaintainability, "Maintainability");
        addStandardCol(BuildRecap::getHotspots, "Hotspots");
        addStandardCol(BuildRecap::getCoverage, "Coverage");
        addStandardCol(BuildRecap::getDuplications, "Duplications");

        grid.sort(
            new GridSortOrderBuilder<BuildRecap>().thenDesc(time).build()
        );

        refresh();
        return grid;
    }

    public void refresh() {
        grid.setItems(BuildHistory.history());
    }

    private Column<BuildRecap> addStandardCol(
        ValueProvider<BuildRecap, String> value,
        String header
    ) {
        return grid.addColumn(value).setHeader(header).setAutoWidth(true);
    }
}
