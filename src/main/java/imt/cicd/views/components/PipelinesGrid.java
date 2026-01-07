package imt.cicd.views.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import imt.cicd.data.BuildHistory;
import imt.cicd.data.BuildHistory.BuildRecap;

public class PipelinesGrid extends Composite<Grid<BuildRecap>> {

    private final Grid<BuildRecap> grid = new Grid<>(BuildRecap.class);

    @Override
    protected Grid<BuildRecap> initContent() {
        grid.getColumns().stream().forEach(column -> column.setResizable(true));

        refresh();
        return grid;
    }

    public void refresh() {
        grid.setItems(BuildHistory.history());
    }
}
