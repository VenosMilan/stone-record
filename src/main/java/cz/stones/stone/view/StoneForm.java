package cz.stones.stone.view;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import cz.stones.stone.stones.model.StateOfStone;
import cz.stones.stone.stones.service.pojo.StonePojo;
import cz.stones.stone.view.convertor.DoubleToBigDecimalConverter;

public class StoneForm extends FormLayout {

  private StonePojo stone;

  Button save = new Button("Save");
  Button close = new Button("Cancel");

  BeanValidationBinder<StonePojo> binder = new BeanValidationBinder<>(StonePojo.class);

  public StoneForm() {
    addClassName("editor-layout");
    stone = new StonePojo();

    TextField manufactureField = ViewTools.getTextField("manufacture");
    binder.forField(manufactureField).asRequired("Element is required").bind("manufacture");

    TextField colorField = ViewTools.getTextField("color");
    binder.forField(colorField).asRequired("Element is required").bind("color");

    TextField flatDimensionsField = ViewTools.getTextField("flatDimensions");
    flatDimensionsField.setLabel("DIMENSIONS");
    flatDimensionsField.setPattern("^(\\d+(\\.\\d+)?x)+\\d+(\\.\\d+)?$");
    flatDimensionsField.setHelperText("Possible values: 10x20, 10.1x20.5, 15X20,...");
    binder.forField(flatDimensionsField).asRequired("Element is required").bind("flatDimensions");

    TextField rackField = ViewTools.getTextField("rack");
    binder.forField(rackField).bind("rack");
    TextField notesField = ViewTools.getTextField("notes");
    binder.forField(notesField).bind("notes");

    NumberField thicknesField = ViewTools.getNumberField("thicknes");
    binder.forField(thicknesField).withConverter(new DoubleToBigDecimalConverter())
        .asRequired("Element is required").bind("thicknes");

    ComboBox<?> stateOfStonesField = ViewTools.getComboBox("stateOfStone", StateOfStone.values());
    binder.forField(stateOfStonesField).bind("stateOfStone");


    add(manufactureField);
    add(colorField);
    add(thicknesField);
    add(flatDimensionsField);
    add(stateOfStonesField);
    add(rackField);
    add(notesField);

    add(createButtonsLayout());
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickShortcut(Key.ENTER);
    close.addClickShortcut(Key.ESCAPE);

    save.addClickListener(event -> validateAndSave());
    close.addClickListener(event -> fireEvent(new CloseEvent(this)));

    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

    return new HorizontalLayout(save, close);
  }

  public void setStone(StonePojo stone) {
    this.stone = stone;
    binder.readBean(stone);
  }

  private void validateAndSave() {
    try {
      binder.validate();
      binder.writeBean(stone);
      fireEvent(new SaveEvent(this, stone));
      fireEvent(new CloseEvent(this));
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }

  public static abstract class StoneEditorFormEvent extends ComponentEvent<StoneForm> {
    private StonePojo stone;

    protected StoneEditorFormEvent(StoneForm source, StonePojo stone) {
      super(source, false);
      this.stone = stone;
    }

    public StonePojo getStone() {
      return stone;
    }
  }

  public static class SaveEvent extends StoneEditorFormEvent {
    SaveEvent(StoneForm source, StonePojo stone) {
      super(source, stone);
    }
  }

  public static class DeleteEvent extends StoneEditorFormEvent {
    DeleteEvent(StoneForm source, cz.stones.stone.stones.service.pojo.StonePojo stone) {
      super(source, stone);
    }

  }

  public static class CloseEvent extends StoneEditorFormEvent {
    CloseEvent(StoneForm source) {
      super(source, new StonePojo());
    }
  }

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
      ComponentEventListener<T> listener) {
    return getEventBus().addListener(eventType, listener);
  }

}
