package test.technical.librarian.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import test.technical.librarian.constant.PssConstant;
import test.technical.librarian.dto.request.BookRequest;
import test.technical.librarian.dto.request.PssFilter;
import test.technical.librarian.dto.response.BookResponse;
import test.technical.librarian.exception.RestRuntimeException;
import test.technical.librarian.mapper.BookMapper;
import test.technical.librarian.model.Book;
import test.technical.librarian.repository.BookRepository;
import test.technical.librarian.service.BookService;
import test.technical.librarian.utils.ServiceUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static test.technical.librarian.constant.ErrorCode.*;

@Transactional
@Service
@Slf4j
public class IBookService extends ServiceUtils implements BookService {

    private final BookRepository dao;
    private final BookMapper mapper;
    private final EntityManager entityManager;

    @Autowired
    public IBookService(BookRepository dao, BookMapper mapper, EntityManager entityManager) {
        this.dao = dao;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Override
    public BookResponse save(BookRequest s) {
        Book toSave = mapper.fromDto(s);
        toSave.setActive(true);
        try {
            Book saved = dao.save(toSave);
            return mapper.toDto(saved);
        } catch (Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal menyimpan data buku", e);
        }
    }

    @Override
    public Optional<BookResponse> update(BookRequest s) {
        Book indb = this.getOne(s.getId());
        Book oldObj = SerializationUtils.clone(indb);
        Book toSave = mapper.fromDto(s);
        setCommonValue(indb, toSave);

        try {
            Book saved = dao.save(toSave);
            return Optional.of(mapper.toDto(saved));
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal mengupdate data buku", e);
        }
    }

    @Override
    public Optional<BookResponse> findOne(String id) {
        return Optional.of(mapper.toDto(this.getOne(id)));
    }

    @Override
    public void delete(String id) {
        Book indb = this.getOne(id);
        validateRecordBeforeUpdate(indb);
        dao.delete(indb);
    }

    @Override
    public List<BookResponse> filter(PssFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        if(filter.getSearch()!=null &&
                StringUtils.isNotBlank(filter.getSearch().get(PssConstant.PSS_SEARCH_VAL))) {
            Predicate orPredicate = cb.or(datatablesPredicate(filter, cb, root));
            predicates.add(orPredicate);

        }
        cq.where(predicates.toArray(new Predicate[predicates.size()]));

        doSorting(filter, cq, cb, root);

        TypedQuery<Book> query = entityManager.createQuery(cq);
        List<Book> results = query
                .setFirstResult((filter.getStart()))
                .setMaxResults(filter.getLength())
                .getResultList();

        return mapper.toListDto(results);
    }

    @Override
    public Long count(PssFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> count = cb.createQuery(Long.class);
        Root<Book> root = count.from(Book.class);
        count.select(cb.count(root));

        List<Predicate> predicates = new ArrayList<Predicate>();
        if(filter.getSearch()!=null &&
                StringUtils.isNotBlank(filter.getSearch().get(PssConstant.PSS_SEARCH_VAL))) {
            Predicate orPredicate = cb.or(datatablesPredicate(filter, cb, root));
            predicates.add(orPredicate);

        }
        count.where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(count).getSingleResult();
    }

    private Predicate[] datatablesPredicate(PssFilter filter, CriteriaBuilder cb, Root<Book> root) {
        Predicate p1 = cb.like(cb.lower(root.get("name")), likeSearchValue(filter));
        return new Predicate[] {p1};
    }

    private void doSorting(PssFilter filter, CriteriaQuery<Book> cq, CriteriaBuilder cb, Root<Book> root) {
        Sort.Direction direction = Sort.Direction.ASC;

        List<Order> ordersBy = new ArrayList<>();
        if(!filter.getOrder().isEmpty()) {
            String colidx = filter.getOrder().get(0).get(PssConstant.PSS_ORDER_COLUMN);
            direction = Sort.Direction.fromString(filter.getOrder().get(0).get(PssConstant.PSS_ORDER_DIRECTION));

            switch(Integer.parseInt(colidx)) {
                case 0:
                    ordersBy.add(cb.asc(root.get("id"))); break;
                case 1:
                    ordersBy.add(cb.asc(root.get("name"))); break;
                default :
                    ordersBy.add(cb.asc(root.get("id"))); break;
            }
        }

        log.debug("Get Book order by {}-{}", ordersBy, direction);

        if(direction.isAscending()) {
            cq.orderBy(ordersBy);
        } else if(direction.isDescending()) {
            for (Order order : ordersBy) {
                order.reverse();
            }
            cq.orderBy(ordersBy);
        }
    }

    @Override
    public void setDelete(String id) {
        Book indb = this.getOne(id);
        validateRecordBeforeUpdate(indb);

        try {
            indb.setDeleted(true);
            dao.save(indb);
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal menghapus Book", e);
        }
    }


    @Override
    public void setActive(String id, Boolean active) {
        Book indb = this.getOne(id);
        validateRecordBeforeUpdate(indb);

        try {
            indb.setActive(active);
            dao.save(indb);
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal mengupdate status active data buku", e);
        }
    }

    private Book getOne(String id) {
        Optional<Book> op = dao.findById(id);
        if(!op.isPresent()) {
            throw new RestRuntimeException(DATA_NOTFOUND, "book with id:"+id+" not found");
        }
        return op.get();
    }
}
