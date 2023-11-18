package cz.stones.stone.stones.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import cz.stones.stone.stones.dao.StoneDao;
import cz.stones.stone.stones.model.Dimension;
import cz.stones.stone.stones.model.Stone;
import cz.stones.stone.stones.service.exception.StoneException;
import cz.stones.stone.stones.service.pojo.FilterPojo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class StoneDaoImpl implements StoneDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long createStone(Stone stone) {
        try {
            entityManager.persist(stone);
        } catch (Exception e) {
            throw new StoneException("Stone persist failed.", e);
        }
        return stone.getId();
    }

    @Override
    public Stone getStone(Long id) {
        Optional<Stone> stone;

        try {
            stone = Optional.ofNullable(entityManager.find(Stone.class, id));
        } catch (Exception e) {
            throw new StoneException("Get stone failed.", e);
        }

        if (stone.isEmpty()) {
            throw new StoneException("Stone by id %d not found", id);
        }

        return stone.get();
    }

    @Override
    public void deleteStone(Long id) {
        try {
            entityManager.remove(getStone(id));
        } catch (Exception e) {
            throw new StoneException("Delete stone failed.", e);
        }
    }

    @Override
    public void updateStone(Stone stone) {
        entityManager.merge(stone);
    }

    @Override
    public Page<Stone> list(Pageable pageable, FilterPojo filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Stone> cq = cb.createQuery(Stone.class);
        Root<Stone> root = cq.from(Stone.class);

        List<Predicate> predicates = new ArrayList<>();
       
       cq.select(root);

        if (filter != null && !filter.getTextFilter().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("manufacture")), "%" + filter.getTextFilter().toLowerCase() + "%"));
            predicates.add(cb.like(cb.lower(root.get("color")), "%" + filter.getTextFilter().toLowerCase() + "%"));
            
            cq.where(cb.or(predicates.toArray(new Predicate[0])));
        }


        cq.orderBy(cb.asc(root.get("manufacture")), cb.asc(root.get("stateOfStone")));

        TypedQuery<Stone> query = entityManager.createQuery(cq);
        List<Stone> stones = query.getResultList();

        return new PageImpl<>(stones);
    }

    @Override
    public Long count() {
        return 0L;
    }

    @Override
    public Long createDimension(Dimension dimension) {
        entityManager.persist(dimension);
        return dimension.getId();
    }

    @Override
    public void deleteDimension(Dimension dimension) {
        entityManager.remove(getDimension(dimension));
    }

    @Override
    public void merge(Stone stone) {
        entityManager.merge(stone);
    }

    private Dimension getDimension(Dimension dimension) {
        Optional<Dimension> dim;

        try {
            dim = Optional.ofNullable(entityManager.find(Dimension.class, dimension.getId()));
        } catch (Exception e) {
            throw new StoneException("Get dimension failed.", e);
        }

        if (dim.isEmpty()) {
            throw new StoneException("Stone by id %d not found", dimension.getId());
        }

        return dim.get();
    }

}
