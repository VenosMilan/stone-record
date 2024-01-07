package cz.stones.stone.view.convertor;

import java.math.BigDecimal;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;


public class DoubleToBigDecimalConverter implements Converter<Double, BigDecimal> {


    @Override
    public Result<BigDecimal> convertToModel(Double value, ValueContext context) {
        return value == null ? Result.ok(null) : Result.ok(BigDecimal.valueOf(value));
    }

    @Override
    public Double convertToPresentation(BigDecimal value, ValueContext context) {
        return value == null ? null : value.doubleValue();
    }
}

