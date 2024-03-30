package cz.stones.stone.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import cz.stones.stone.stones.service.CsvService;
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
    private final CsvService csvService;
    private StoneForm form;
    private Dialog dialog;

    public MainView(StoneService stoneService, CsvService csvService) {
        this.stoneService = stoneService;
        this.csvService = csvService;

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
        grid.addColumn(StonePojo::getId).setHeader("Id").setFrozen(false).setAutoWidth(true).setVisible(false);
        Grid.Column<StonePojo> manufactureColumn = grid.addColumn(StonePojo::getManufacture)
                .setHeader("Manufacture").setFrozen(false).setAutoWidth(true);;
        Grid.Column<StonePojo> colorColumn = grid.addColumn(StonePojo::getColor).setHeader("Color")
                .setFrozen(false);
        Grid.Column<StonePojo> thicknesColumn = grid.addColumn(StonePojo::getThicknes)
                .setHeader("Thicknes").setFrozen(false);
        Grid.Column<StonePojo> dimensionsColumn = grid.addColumn(StonePojo::getFlatDimensions)
                .setHeader("Dimensions").setFrozen(false).setAutoWidth(true);
        Grid.Column<StonePojo> stateOfStoneColumn = grid.addColumn(StonePojo::getStateOfStone)
                .setHeader("State of stone").setFrozen(false).setAutoWidth(true);
        Grid.Column<StonePojo> rackColumn = grid.addColumn(StonePojo::getRack).setHeader("Rack")
                .setFrozen(false).setAutoWidth(true);
        Grid.Column<StonePojo> notesColumn = grid.addColumn(StonePojo::getNotes).setHeader("Notes")
                .setFrozen(false).setAutoWidth(true);
        Grid.Column<StonePojo> editColumn = addEditButton();
        editColumn.setAutoWidth(true);

        prepareEditGridEditFields(binder, manufactureColumn, colorColumn, thicknesColumn,
                dimensionsColumn, stateOfStoneColumn, rackColumn, notesColumn);
        prepareRowEditAction(editColumn);
    }

    private void prepareRowEditAction(Grid.Column<StonePojo> editColumn) {
        Button saveButton = new Button("Save", e -> {
            editor.save();
            List<StonePojo> list = new ArrayList<>();

            for (int i = 0; i < grid.getDataCommunicator().getItemCount(); i++) {
                list.add(grid.getDataCommunicator().getItem(i));
            }

            this.csvService.saveDataToCsv(convertToArrayList(list), "./data.csv");
        });

        saveButton.setTooltipText("Save record");

        Button deleteButton = new Button("Delete", e -> {
            stoneService.deleteStone(editor.getItem().getId());
            editor.cancel();
            grid.getDataProvider().refreshAll();
            List<StonePojo> list = new ArrayList<>();

            for (int i = 0; i < grid.getDataCommunicator().getItemCount(); i++) {
                list.add(grid.getDataCommunicator().getItem(i));
            }

            this.csvService.saveDataToCsv(convertToArrayList(list), "./data.csv");
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
        }).setHeader("Action").setFlexGrow(0);
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
        List<StonePojo> list = new ArrayList<>();

        for (int i = 0; i < grid.getDataCommunicator().getItemCount(); i++) {
            list.add(grid.getDataCommunicator().getItem(i));
        }

        this.csvService.saveDataToCsv(convertToArrayList(list), "./data.csv");
    }

    private void closeDialog() {
        dialog.close();
    }


    public static List<String> convertToArrayList(Collection<StonePojo> pojoList) {
        List<String> result = new ArrayList<>();

        pojoList.forEach(pojo -> {
            List<String> resultList = new ArrayList<>();

            if (pojo == null) {
                System.out.println("null");
            }

            if (pojo.getId() != null) {
                resultList.add(pojo.getId().toString());
            }

            if (pojo.getManufacture() != null && !pojo.getManufacture().isEmpty()) {
                resultList.add(pojo.getManufacture());
            }

            if (pojo.getColor() != null && !pojo.getColor().isEmpty()) {
                resultList.add(pojo.getColor());
            }

            if (pojo.getNotes() != null) {
                resultList.add(pojo.getNotes().toString());
            }

            if (pojo.getRack() != null) {
                resultList.add(pojo.getRack().toString());
            }

            if (pojo.getThicknes() != null) {
                resultList.add(pojo.getThicknes().toString());
            }

            if (pojo.getThicknes() != null) {
                resultList.add(pojo.getThicknes().toString());
            }

            if (pojo.getFlatDimensions() != null && !pojo.getFlatDimensions().isEmpty()) {
                resultList.add(pojo.getFlatDimensions());
            }

            result.add(String.join(";", resultList));
        });


        return result;
    }
}

