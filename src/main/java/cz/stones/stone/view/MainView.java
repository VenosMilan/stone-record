package cz.stones.stone.view;

import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.stones.stone.stones.model.StateOfStone;
import cz.stones.stone.stones.service.StoneService;
import cz.stones.stone.stones.service.pojo.FilterPojo;
import cz.stones.stone.stones.service.pojo.StonePojo;
import cz.stones.stone.view.component.ColumnToogleContextMenu;
import cz.stones.stone.view.convertor.DoubleToBigDecimalConverter;

@PageTitle("List All")
@Route(layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    private final Grid<StonePojo> grid = new Grid<>(StonePojo.class, false);
    private final Editor<StonePojo> editor = grid.getEditor();
    private final StoneService stoneService;
    private StoneForm form;
    private Dialog dialog;
    private TextField filterText = new TextField();
    private TextField dimensionFilterText = new TextField();
    private FilterPojo filter;

    public MainView(StoneService stoneService) {
        this.stoneService = stoneService;
       // addClassName("list-all-view");

        configureForm();
        configureDialog();

        setSizeFull();
        configureGrid();

        add(prepareToolbar(), grid);
    }

    private void configureGrid() {
        var binder = new BeanValidationBinder<>(StonePojo.class);

        editor.setBinder(binder);
        editor.setBuffered(true);

        editor.addSaveListener(event -> {
            StonePojo item = event.getItem();
            stoneService.updateStone(item);
        });

        prepareColumns(binder);

        grid.setItems(
                query -> stoneService.list(PageRequest.of(query.getPage(), query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query)), null).stream());

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void prepareColumns(BeanValidationBinder<StonePojo> binder) {
        grid.addColumn(StonePojo::getId)
                .setHeader("Id").setFrozen(true).setWidth("5%").setFlexGrow(0).setVisible(false);
        Grid.Column<StonePojo> manufactureColumn = grid.addColumn(StonePojo::getManufacture)
                .setHeader("Manufacture").setFrozen(true).setWidth("10%").setFlexGrow(0);
        Grid.Column<StonePojo> colorColumn = grid.addColumn(StonePojo::getColor).setHeader("Color")
                .setFrozen(true).setWidth("10%").setFlexGrow(0);
        Grid.Column<StonePojo> thicknesColumn = grid.addColumn(StonePojo::getThicknes)
                .setHeader("Thicknes").setFrozen(true).setWidth("10%").setFlexGrow(0);
        Grid.Column<StonePojo> dimensionsColumn = grid.addColumn(StonePojo::getFlatDimensions)
                .setHeader("Dimensions").setFrozen(true).setAutoWidth(true).setFlexGrow(0);
        Grid.Column<StonePojo> stateOfStoneColumn = grid.addColumn(StonePojo::getStateOfStone)
                .setHeader("State of stone").setFrozen(true).setWidth("15%").setFlexGrow(0);
        Grid.Column<StonePojo> rackColumn = grid.addColumn(StonePojo::getRack).setHeader("Rack")
                .setFrozen(true).setWidth("10%").setFlexGrow(0);
        Grid.Column<StonePojo> notesColumn = grid.addColumn(StonePojo::getNotes).setHeader("Notes")
                .setFrozen(true).setWidth("10%").setFlexGrow(0);
        Grid.Column<StonePojo> editColumn = addEditButton();
        editColumn.setWidth("30%");

        prepareEditGridEditFields(binder, manufactureColumn, colorColumn, thicknesColumn,
                dimensionsColumn, stateOfStoneColumn, rackColumn, notesColumn);
        prepareRowEditAction(editColumn);
    }

    private void prepareRowEditAction(Grid.Column<StonePojo> editColumn) {
        Button saveButton = new Button("Save", e -> editor.save());
        saveButton.setTooltipText("Save record");

        Button deleteButton = new Button("Delete", e -> {
            stoneService.deleteStone(editor.getItem().getId());
            editor.cancel();
            grid.getDataProvider().refreshAll();
        });

        deleteButton.setTooltipText("Delete record");

        Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        cancelButton.setTooltipText("Cancel");

        HorizontalLayout actions = new HorizontalLayout(saveButton, deleteButton, cancelButton);
        actions.setPadding(false);
        editColumn.setEditorComponent(actions);
    }

    private void prepareEditGridEditFields(BeanValidationBinder<StonePojo> binder,
            Grid.Column<StonePojo> manufactureColumn, Grid.Column<StonePojo> colorColumn,
            Grid.Column<StonePojo> thicknesColumn, Grid.Column<StonePojo> dimensionsColumn,
            Grid.Column<StonePojo> stateOfStoneColumn, Grid.Column<StonePojo> rackColumn,
            Grid.Column<StonePojo> notesColumn) {
        TextField manufactureField = ViewTools.getTextField("manufacture");
        manufactureField.setLabel(null);
        binder.forField(manufactureField).asRequired("Element is required").bind("manufacture");
        manufactureColumn.setEditorComponent(manufactureField);

        TextField colorField = ViewTools.getTextField("color");
        colorField.setLabel(null);
        binder.forField(colorField).asRequired("Element is required").bind("color");
        colorColumn.setEditorComponent(colorField);

        TextField flatDimensionsField = ViewTools.getTextField("flatDimensions");
        flatDimensionsField.setLabel(null);
        flatDimensionsField.setPattern("^(\\d+(\\.\\d+)?x)+\\d+(\\.\\d+)?$");
        flatDimensionsField.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        binder.forField(flatDimensionsField).asRequired("Element is required")
                .bind("flatDimensions");
        dimensionsColumn.setEditorComponent(flatDimensionsField);

        TextField rackField = ViewTools.getTextField("rack");
        rackField.setLabel(null);
        binder.forField(rackField).bind("rack");
        rackColumn.setEditorComponent(rackField);

        TextField notesField = ViewTools.getTextField("notes");
        notesField.setLabel(null);
        binder.forField(notesField).bind("notes");
        notesColumn.setEditorComponent(notesField);

        NumberField thicknesField = ViewTools.getNumberField("thicknes");
        thicknesField.setLabel(null);
        binder.forField(thicknesField).withConverter(new DoubleToBigDecimalConverter())
                .asRequired("Element is required").bind("thicknes");
        thicknesColumn.setEditorComponent(thicknesField);

        ComboBox<?> stateOfStonesField = ViewTools.getComboBox("stateOfStone", StateOfStone.values());
        stateOfStonesField.setLabel(null);
        binder.forField(stateOfStonesField).bind("stateOfStone");
        stateOfStoneColumn.setEditorComponent(stateOfStonesField);
    }

    private Grid.Column<StonePojo> addEditButton() {
        Grid.Column<StonePojo> editColumn = grid.addComponentColumn(stone -> {
            Button editButton = new Button("Edit");
            editButton.setTooltipText("Edit record");
            editButton.addClickListener(e -> {
                if (editor.isOpen()) {
                    editor.cancel();
                }
                grid.getEditor().editItem(stone);
            });
            return editButton;
        }).setWidth("150px").setHeader("Action").setFlexGrow(0);
        return editColumn;
    }

    private HorizontalLayout prepareToolbar() {
        textFilter();
        dimensionFilter();

        Button addContactButton = new Button("Add stone");
        addContactButton.addClickListener(e -> {
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

        var horizontalLayout = new HorizontalLayout(filterText, dimensionFilterText, addContactButton);
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

    private void dimensionFilter() {
        dimensionFilterText.setPlaceholder("Dimension filter ...");
        dimensionFilterText.setClearButtonVisible(true);
        dimensionFilterText.setValueChangeMode(ValueChangeMode.LAZY);

        dimensionFilterText.addValueChangeListener(
                e -> updateList(new FilterPojo(this.dimensionFilterText.getValue())));
    }

    private void updateList(FilterPojo filter) {
        grid.setItems(query -> {
            MainView.this.filter = filter;
            return stoneService
                    .list(PageRequest.of(query.getPage(), query.getPageSize(),
                            VaadinSpringDataHelpers.toSpringDataSort(query)), MainView.this.filter)
                    .stream();
        });
    }

    private void configureForm() {
        form = new StoneForm();
        form.setWidth("25em");
        form.addListener(StoneForm.SaveEvent.class, this::saveStone);
        form.addListener(StoneForm.CloseEvent.class, e -> {
            closeDialog();
            this.grid.getDataProvider().refreshAll();
        });
    }

    private void configureDialog() {
        dialog = new Dialog();
        dialog.setModal(true);
        dialog.setWidth("auto");
        dialog.add(form);
    }

    private void saveStone(StoneForm.SaveEvent event) {
        this.stoneService.createStone(event.getStone());
    }

    private void closeDialog() {
        dialog.close();
    }
}

