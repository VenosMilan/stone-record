package cz.stones.stone.stones.model;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Dimension implements Serializable {

    private static final long serialVersionUID = 71122768818150L;

    @Id
    @GeneratedValue(generator = "dimensions_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "dimensions_id_seq", sequenceName = "dimensions_id_seq",
            allocationSize = 10)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stone_id")
    @NotNull
    private Stone stone;

    @NotNull
    @Column(precision = 15, scale = 2)
    private BigDecimal dimension;

}
