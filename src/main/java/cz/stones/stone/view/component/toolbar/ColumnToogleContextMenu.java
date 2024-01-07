package cz.stones.stone.view.component.toolbar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import cz.stones.stone.stones.service.pojo.StonePojo;

public class ColumnToogleContextMenu extends ContextMenu {

    public ColumnToogleContextMenu(Component target) {
        super(target);
        setOpenOnClick(true);
        setVisible(true);
    }

    public void addColumnToggleItem(String label, Grid.Column<StonePojo> column) {
        MenuItem menuItem = this.addItem(label, e -> {
            column.setVisible(e.getSource().isChecked());
        });
        menuItem.setCheckable(true);
        menuItem.setChecked(column.isVisible());
    }

}
