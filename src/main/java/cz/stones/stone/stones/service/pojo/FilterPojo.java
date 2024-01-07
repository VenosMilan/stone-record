package cz.stones.stone.stones.service.pojo;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterPojo {

    private String textFilter;

    private BigDecimal thicknesFilter;

    private String dimensionFilter;

    public FilterPojo() {}


    public FilterPojo(String input) {
        setTextFilter(input);
    }

    public FilterPojo(BigDecimal input) {
        setThicknesFilter(input);
    }


}
