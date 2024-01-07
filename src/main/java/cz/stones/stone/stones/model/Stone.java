
package cz.stones.stone.stones.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stone implements Serializable {

    private static final long serialVersionUID = 71442768818150L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private String manufacture;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StateOfStone stateOfStone = StateOfStone.AVAILABLE;

    @NotNull
    @Column(precision = 15, scale = 2)
    private BigDecimal thicknes;

    @NotNull
    private String color;

    private String notes;

    private String rack;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateOfCreation;

    @OneToMany(mappedBy = "stone", cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.LAZY)
    @NotNull
    private List<Dimension> dimensions;

    @NotNull
    private Integer countOfDimensions;
}
