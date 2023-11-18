package cz.stones.stone.stones.service.pojo;

import java.math.BigDecimal;
import cz.stones.stone.stones.model.Dimension;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DimensionPojo {

    private Long id;

    private BigDecimal dimension;

    public DimensionPojo(Dimension dimension) {
        this.setId(dimension.getId());
        this.setDimension(dimension.getDimension());
    }

}
