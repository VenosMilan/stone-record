package cz.stones.stone.stones.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cz.stones.stone.stones.service.pojo.FilterPojo;
import cz.stones.stone.stones.service.pojo.StonePojo;

public interface StoneService {

    Long createStone(StonePojo stonePojo);

    StonePojo getStone(Long id);

    void deleteStone(Long id);

    void updateStone(StonePojo stonePojo);

    Page<StonePojo> list(Pageable pageable, FilterPojo filter);
    
}
