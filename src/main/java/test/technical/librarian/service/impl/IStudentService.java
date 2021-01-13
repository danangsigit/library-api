package test.technical.librarian.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import test.technical.librarian.constant.PssConstant;
import test.technical.librarian.dto.request.PssFilter;
import test.technical.librarian.dto.request.StudentRequest;
import test.technical.librarian.dto.response.StudentResponse;
import test.technical.librarian.exception.RestRuntimeException;
import test.technical.librarian.mapper.StudentMapper;
import test.technical.librarian.model.Student;
import test.technical.librarian.repository.StudentRepository;
import test.technical.librarian.service.StudentService;
import test.technical.librarian.utils.ServiceUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static test.technical.librarian.constant.ErrorCode.DATA_NOTFOUND;
import static test.technical.librarian.constant.ErrorCode.UNKNOWN_ERROR;

@Transactional
@Service
@Slf4j
public class IStudentService extends ServiceUtils implements StudentService {

    private final StudentRepository dao;
    private final StudentMapper mapper;
    private final EntityManager entityManager;

    @Autowired
    public IStudentService(StudentRepository dao, StudentMapper mapper, EntityManager entityManager) {
        this.dao = dao;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Override
    public StudentResponse save(StudentRequest s) {
        Student toSave = mapper.fromDto(s);
        toSave.setActive(true);
        try {
            Student saved = dao.save(toSave);
            return mapper.toDto(saved);
        } catch (Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal menyimpan data Student", e);
        }
    }

    @Override
    public Optional<StudentResponse> update(StudentRequest s) {
        Student indb = this.getOne(s.getId());
        Student oldObj = SerializationUtils.clone(indb);
        Student toSave = mapper.fromDto(s);
        setCommonValue(indb, toSave);

        try {
            Student saved = dao.save(toSave);
            return Optional.of(mapper.toDto(saved));
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal mengupdate data Student", e);
        }
    }

    @Override
    public Optional<StudentResponse> findOne(String id) {
        return Optional.of(mapper.toDto(this.getOne(id)));
    }

    @Override
    public void delete(String id) {
        Student indb = this.getOne(id);
        validateRecordBeforeUpdate(indb);
        dao.delete(indb);
    }

    @Override
    public List<StudentResponse> filter(PssFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        Root<Student> root = cq.from(Student.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        if(filter.getSearch()!=null &&
                StringUtils.isNotBlank(filter.getSearch().get(PssConstant.PSS_SEARCH_VAL))) {
            Predicate orPredicate = cb.or(datatablesPredicate(filter, cb, root));
            predicates.add(orPredicate);

        }
        cq.where(predicates.toArray(new Predicate[predicates.size()]));

        doSorting(filter, cq, cb, root);

        TypedQuery<Student> query = entityManager.createQuery(cq);
        List<Student> results = query
                .setFirstResult((filter.getStart()))
                .setMaxResults(filter.getLength())
                .getResultList();

        return mapper.toListDto(results);
    }

    @Override
    public Long count(PssFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> count = cb.createQuery(Long.class);
        Root<Student> root = count.from(Student.class);
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

    private Predicate[] datatablesPredicate(PssFilter filter, CriteriaBuilder cb, Root<Student> root) {
        Predicate p1 = cb.like(cb.lower(root.get("name")), likeSearchValue(filter));
        return new Predicate[] {p1};
    }

    private void doSorting(PssFilter filter, CriteriaQuery<Student> cq, CriteriaBuilder cb, Root<Student> root) {
        Sort.Direction direction = Sort.Direction.ASC;

        List<Order> ordersBy = new ArrayList<>();
        if(filter.getOrder() != null && !filter.getOrder().isEmpty()) {
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

        log.debug("Get Student order by {}-{}", ordersBy, direction);

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
        Student indb = this.getOne(id);
        validateRecordBeforeUpdate(indb);

        try {
            indb.setDeleted(true);
            dao.save(indb);
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal menghapus Student", e);
        }
    }


    @Override
    public void setActive(String id, Boolean active) {
        Student indb = this.getOne(id);
        validateRecordBeforeUpdate(indb);

        try {
            indb.setActive(active);
            dao.save(indb);
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal mengupdate status active data Student", e);
        }
    }

    private Student getOne(String id) {
        Optional<Student> op = dao.findById(id);
        if(!op.isPresent()) {
            log.error("Student with id:{} not found", id);
            return null;
        }
        return op.get();
    }
}
