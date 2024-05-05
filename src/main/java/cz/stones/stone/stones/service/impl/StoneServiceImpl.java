package cz.stones.stone.stones.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cz.stones.stone.stones.dao.StoneDao;
import cz.stones.stone.stones.model.Dimension;
import cz.stones.stone.stones.model.Stone;
import cz.stones.stone.stones.service.StoneService;
import cz.stones.stone.stones.service.exception.StoneException;
import cz.stones.stone.stones.service.pojo.FilterPojo;
import cz.stones.stone.stones.service.pojo.StonePojo;

@Service
public class StoneServiceImpl implements StoneService {

    @Autowired
    private StoneDao stoneDao;

    @Override
    @Transactional
    public Long createStone(StonePojo stonePojo) {
        Stone stone = new Stone();

        stone.setManufacture(stonePojo.getManufacture());
        stone.setStateOfStone(stonePojo.getStateOfStone());
        stone.setThicknes(stonePojo.getThicknes());
        stone.setColor(stonePojo.getColor());
        stone.setNotes(stonePojo.getNotes());
        stone.setRack(stonePojo.getRack());
        stone.setDateOfCreation(LocalDateTime.now());
        stone.setDimensions(new ArrayList<>());

        List.of(stonePojo.getFlatDimensions().toLowerCase().split("x")).forEach(d -> {
            d = d.trim();
            
            Dimension dim = new Dimension();
            dim.setDimension(new BigDecimal(d));
            dim.setStone(stone);

            stone.getDimensions().add(dim);
        });

        stone.setCountOfDimensions(stone.getDimensions().size());

        return this.stoneDao.createStone(stone);
    }

    @Override
    @Transactional(readOnly = true)
    public StonePojo getStone(Long id) {
        Stone stone = this.stoneDao.getStone(id);

        if (stone != null) {
            return new StonePojo(stone);
        }

        throw new StoneException("Stone %d not found", id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteStone(Long id) {
        this.stoneDao.deleteStone(id);
    }

    @Override
    @Transactional
    public void updateStone(StonePojo stonePojo) {
        Stone stone = stoneDao.getStone(stonePojo.getId());

        stone.setManufacture(stonePojo.getManufacture());
        stone.setStateOfStone(stonePojo.getStateOfStone());
        stone.setThicknes(stonePojo.getThicknes());
        stone.setColor(stonePojo.getColor());
        stone.setNotes(stonePojo.getNotes());
        stone.setRack(stonePojo.getRack());

        stone.getDimensions().removeAll(stone.getDimensions());

        List.of(stonePojo.getFlatDimensions().toLowerCase().split("x")).forEach(d -> {
            d = d.trim();

            Dimension dim = new Dimension();
            dim.setDimension(new BigDecimal(d));
            dim.setStone(stone);
            this.stoneDao.createDimension(dim);
            stone.getDimensions().add(dim);
        });


        stone.setCountOfDimensions(stone.getDimensions().size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StonePojo> list(Pageable pageable, FilterPojo filter) {
        return stoneDao.list(pageable, filter).map(StonePojo::new);
    }
}
