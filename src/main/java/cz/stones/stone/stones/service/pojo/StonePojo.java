package cz.stones.stone.stones.service.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import cz.stones.stone.stones.model.StateOfStone;
import cz.stones.stone.stones.model.Stone;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StonePojo {

    private Long id;

    private String manufacture;

    private StateOfStone stateOfStone = StateOfStone.AVAILABLE;

    private BigDecimal thicknes;

    private String color;

    private String notes;

    private String rack;

    private LocalDateTime dateOfCreation;

    private List<DimensionPojo> dimensions = new ArrayList<>();

    private String flatDimensions;

    public StonePojo() {}

    public StonePojo(Stone stone) {
        this.setId(stone.getId());
        this.setManufacture(stone.getManufacture());
        this.setStateOfStone(stone.getStateOfStone());
        this.setThicknes(stone.getThicknes());
        this.setColor(stone.getColor());
        this.setNotes(stone.getNotes());
        this.setRack(stone.getRack());
        this.setDateOfCreation(stone.getDateOfCreation());
        this.setDimensions(stone.getDimensions().stream().map(DimensionPojo::new).toList());
        this.setFlatDimensions(String.join("x",
                stone.getDimensions().stream().map(v -> v.getDimension().toString()).toList()));
    }
}
