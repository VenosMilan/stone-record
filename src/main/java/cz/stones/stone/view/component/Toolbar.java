package cz.stones.stone.view.component;

import java.math.BigDecimal;
import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.stones.stone.stones.service.StoneService;
import cz.stones.stone.stones.service.pojo.FilterPojo;
import cz.stones.stone.stones.service.pojo.StonePojo;
import cz.stones.stone.view.StoneForm;
import cz.stones.stone.view.component.toolbar.ColumnToogleContextMenu;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Toolbar {

    private Grid<StonePojo> grid;
    private StoneService stoneService;
    private StoneForm form;
    private Dialog dialog;

    private TextField filterText = new TextField();
    private TextField dimensionFilterText = new TextField();
    private NumberField thicknesFilterText = new NumberField();
    private FilterPojo filter;

    public Toolbar(Grid<StonePojo> grid, StoneService stoneService, StoneForm form, Dialog dialog) {
        setDialog(dialog);
        setForm(form);
        setStoneService(stoneService);
        setGrid(grid);
    }

    public HorizontalLayout prepareToolbar() {
        textFilter();
        thicknesFilter();
        dimensionFilter();

        Button addButton = new Button("Add stone");
        addButton.addClickListener(e -> {
            form.setStone(new StonePojo());
            dialog.open();
        });

        Button menuButton = new Button("Show/Hide Columns");
        menuButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        menuButton.setClassName("showHideColumnButton");

        ColumnToogleContextMenu columnToggleContextMenu = new ColumnToogleContextMenu(menuButton);
        grid.getColumns().forEach(col -> {
            columnToggleContextMenu.addColumnToggleItem(col.getHeaderText(), col);
        });

        var horizontalLayout = new HorizontalLayout(filterText, thicknesFilterText, dimensionFilterText, addButton);
        horizontalLayout.setPadding(true);
        horizontalLayout.setFlexGrow(13, menuButton);
        horizontalLayout.add(menuButton);

        return horizontalLayout;
    }

    private void textFilter() {
        filterText.setPlaceholder("Filter ...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        filterText.addValueChangeListener(
                e -> updateList(new FilterPojo(this.filterText.getValue())));
    }

    private void thicknesFilter() {
        thicknesFilterText.setPlaceholder("Thicknes filter ...");
        thicknesFilterText.setClearButtonVisible(true);
        thicknesFilterText.setValueChangeMode(ValueChangeMode.LAZY);

        thicknesFilterText.addValueChangeListener(e -> updateList(
                new FilterPojo(new BigDecimal(this.thicknesFilterText.getValue()))));
    }


    private void dimensionFilter() {
        dimensionFilterText.setPlaceholder("Dimension filter ...");
        dimensionFilterText.setClearButtonVisible(true);
        dimensionFilterText.setValueChangeMode(ValueChangeMode.LAZY);

        dimensionFilterText.addValueChangeListener(
                e -> updateList(new FilterPojo(this.dimensionFilterText.getValue())));
    }

    private void updateList(FilterPojo filter) {
        grid.setItems(query -> {
            Toolbar.this.filter = filter;
            return stoneService
                    .list(PageRequest.of(query.getPage(), query.getPageSize(),
                            VaadinSpringDataHelpers.toSpringDataSort(query)), Toolbar.this.filter)
                    .stream();
        });
    }

}
