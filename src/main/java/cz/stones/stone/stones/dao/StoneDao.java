
package cz.stones.stone.stones.dao;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cz.stones.stone.stones.model.Dimension;
import cz.stones.stone.stones.model.Stone;
import cz.stones.stone.stones.service.pojo.FilterPojo;

public interface StoneDao {

    Long createStone(Stone stone);

    Stone getStone(Long id);

    void deleteStone(Long id);

    void updateStone(Stone stone);

    Page<Stone> list(Pageable pageable, FilterPojo filter);

    Long count();

    Long createDimension(Dimension dimension);

    void deleteDimension(Dimension dimension);

    void merge(Stone stone);

}
