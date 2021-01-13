package test.technical.librarian.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import test.technical.librarian.constant.PssConstant;
import test.technical.librarian.dto.request.LoanRequest;
import test.technical.librarian.dto.request.PssFilter;
import test.technical.librarian.dto.response.LoanResponse;
import test.technical.librarian.exception.RestRuntimeException;
import test.technical.librarian.mapper.BookMapper;
import test.technical.librarian.mapper.LoanMapper;
import test.technical.librarian.mapper.StudentMapper;
import test.technical.librarian.model.Book;
import test.technical.librarian.model.Loan;
import test.technical.librarian.repository.BookRepository;
import test.technical.librarian.repository.LoanRepository;
import test.technical.librarian.repository.StudentRepository;
import test.technical.librarian.service.LoanService;
import test.technical.librarian.utils.ServiceUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static test.technical.librarian.constant.ErrorCode.UNKNOWN_ERROR;

@Transactional
@Service
@Slf4j
public class ILoanService extends ServiceUtils implements LoanService {

    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final LoanRepository dao;

    private final LoanMapper loanMapper;
    private final StudentMapper studentMapper;
    private final BookMapper bookMapper;
    private final EntityManager entityManager;

    @Autowired
    public ILoanService(LoanRepository dao,
                        BookRepository bookRepository,
                        StudentRepository studentRepository,
                        LoanMapper loanMapper,
                        StudentMapper studentMapper,
                        BookMapper bookMapper,
                        EntityManager entityManager) {
        this.dao = dao;
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
        this.loanMapper = loanMapper;
        this.studentMapper = studentMapper;
        this.bookMapper = bookMapper;
        this.entityManager = entityManager;
    }

    @Override
    public LoanResponse save(LoanRequest s) {
        Loan toSave = loanMapper.fromDto(s);
        Set<Book> books = bookRepository.findByIdIn(s.getBookIds());
        toSave.setBooks(books);
        toSave.setStudent(studentRepository.findById(s.getStudentId()).get());
        toSave.setActive(true);
        try {
            Loan saved = dao.save(toSave);
            return loanMapper.toDto(saved);
        } catch (Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal menyimpan loan ", e);
        }
    }

    @Override
    public Optional<LoanResponse> update(LoanRequest s) {
        Loan indb = this.getOne(s.getId());
        Loan oldObj = SerializationUtils.clone(indb);
        Loan toSave = loanMapper.fromDto(s);
        Set<Book> books = bookRepository.findByIdIn(s.getBookIds());
        toSave.setBooks(books);
        toSave.setStudent(studentRepository.findById(s.getStudentId()).get());
        setCommonValue(indb, toSave);

        try {
            Loan saved = dao.save(toSave);
            return Optional.of(loanMapper.toDto(saved));
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal mengupdate data Loan", e);
        }
    }

    @Override
    public Optional<LoanResponse> findOne(String id) {
        Loan loan = this.getOne(id);
        LoanResponse response = loanMapper.toDto(loan);
        response.setStudent(studentMapper.toDto(loan.getStudent()));
        response.setBooks(bookMapper.toListDto(loan.getBooks().stream().collect(Collectors.toList())));
        return Optional.of(response);
    }

    @Override
    public void delete(String id) {
        Loan indb = this.getOne(id);
        validateRecordBeforeUpdate(indb);
        dao.delete(indb);
    }

    @Override
    public List<LoanResponse> filter(PssFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Loan> cq = cb.createQuery(Loan.class);
        Root<Loan> root = cq.from(Loan.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        if(filter.getSearch()!=null &&
                StringUtils.isNotBlank(filter.getSearch().get(PssConstant.PSS_SEARCH_VAL))) {
            Predicate orPredicate = cb.or(datatablesPredicate(filter, cb, root));
            predicates.add(orPredicate);

        }
        cq.where(predicates.toArray(new Predicate[predicates.size()]));

        doSorting(filter, cq, cb, root);

        TypedQuery<Loan> query = entityManager.createQuery(cq);
        List<Loan> results = query
                .setFirstResult((filter.getStart()))
                .setMaxResults(filter.getLength())
                .getResultList();
        List<LoanResponse> responses = loanMapper.toListDto(results);
        return responses;
    }

    @Override
    public Long count(PssFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> count = cb.createQuery(Long.class);
        Root<Loan> root = count.from(Loan.class);
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

    private Predicate[] datatablesPredicate(PssFilter filter, CriteriaBuilder cb, Root<Loan> root) {
        Predicate p1 = cb.like(cb.lower(root.get("id")), likeSearchValue(filter));
        return new Predicate[] {p1};
    }

    private void doSorting(PssFilter filter, CriteriaQuery<Loan> cq, CriteriaBuilder cb, Root<Loan> root) {
        Sort.Direction direction = Sort.Direction.ASC;

        List<Order> ordersBy = new ArrayList<>();
        if(filter.getOrder() != null && !filter.getOrder().isEmpty()) {
            String colidx = filter.getOrder().get(0).get(PssConstant.PSS_ORDER_COLUMN);
            direction = Sort.Direction.fromString(filter.getOrder().get(0).get(PssConstant.PSS_ORDER_DIRECTION));

            ordersBy.add(cb.asc(root.get("id")));
        }

        log.debug("Get Loan order by {}-{}", ordersBy, direction);

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
        Loan indb = this.getOne(id);
        validateRecordBeforeUpdate(indb);

        try {
            indb.setDeleted(true);
            dao.save(indb);
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal menghapus Loan", e);
        }
    }


    @Override
    public void setActive(String id, Boolean active) {
        Loan indb = this.getOne(id);
        validateRecordBeforeUpdate(indb);

        try {
            indb.setActive(active);
            dao.save(indb);
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal mengupdate status active data Loan", e);
        }
    }

    private Loan getOne(String id) {
        Optional<Loan> op = dao.findById(id);
        if(!op.isPresent()) {
            log.error("Loan with id:{} not found", id);
            return null;
        }
        return op.get();
    }
}
