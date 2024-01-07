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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.stones.stone.stones.model.StateOfStone;
import cz.stones.stone.stones.service.StoneService;
import cz.stones.stone.stones.service.pojo.StonePojo;
import cz.stones.stone.view.component.Toolbar;
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

    public MainView(StoneService stoneService) {
        this.stoneService = stoneService;

        configureForm();
        configureDialog();

        setSizeFull();
        configureGrid();

        add(new Toolbar(grid, stoneService, form, dialog).prepareToolbar(), grid);
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
        grid.addColumn(StonePojo::getId).setHeader("Id").setFrozen(true).setWidth("5%")
                .setFlexGrow(0).setVisible(false);
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

        ComboBox<?> stateOfStonesField =
                ViewTools.getComboBox("stateOfStone", StateOfStone.values());
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

