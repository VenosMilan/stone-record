package cz.stones.stone.view;

import org.apache.logging.log4j.util.Strings;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class ViewTools {

        public static TextField getTextField(String atributName) {
                TextField textField = new TextField();
                textField.setWidthFull();
                textField.setLabel(Strings.toRootUpperCase(atributName));

                return textField;
        }

        public static NumberField getNumberField(String atributName) {
                NumberField numberField = new NumberField();
                numberField.setWidthFull();
                numberField.setLabel(Strings.toRootUpperCase(atributName));

                return numberField;
        }

        public static ComboBox<?> getComboBox(String atributName, Enum<?>[] values) {
                ComboBox<Enum<?>> comboBox = new ComboBox<>();
                comboBox.setItems(values);
                comboBox.setWidthFull();
                comboBox.setLabel(Strings.toRootUpperCase(atributName));
                comboBox.setAllowCustomValue(false);

                return comboBox;
        }

}
