package test.technical.librarian.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import test.technical.librarian.constant.ErrorCode;
import test.technical.librarian.constant.PssConstant;
import test.technical.librarian.constant.QueryFilterType;
import test.technical.librarian.dto.request.PssFilter;
import test.technical.librarian.exception.RestException;
import test.technical.librarian.exception.RestRuntimeException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.beans.Expression;
import java.beans.Statement;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ServiceUtils {

    protected void setCommonValue(Object source, Object dest) {
        try {
            Expression expr;
            Statement stmt;

            expr = new Expression(source, "getId", new Object[0]);
            Long id = (Long) expr.getValue();
            stmt = new Statement(dest, "setId", new Object[] {id});
            stmt.execute();

            expr = new Expression(source, "getCreatedBy", new Object[0]);
            String createdBy = (String) expr.getValue();
            stmt = new Statement(dest, "setCreatedBy", new Object[] {createdBy});
            stmt.execute();

            expr = new Expression(source, "getCreatedDate", new Object[0]);
            Date createdDate = (Date) expr.getValue();
            stmt = new Statement(dest, "setCreatedDate", new Object[] {createdDate});
            stmt.execute();

            expr = new Expression(source, "getLastUpdatedBy", new Object[0]);
            String lastUpdatedBy = (String) expr.getValue();
            stmt = new Statement(dest, "setLastUpdatedBy", new Object[] {lastUpdatedBy});
            stmt.execute();

            expr = new Expression(source, "getLastUpdatedDate", new Object[0]);
            Date lastUpdatedDate = (Date) expr.getValue();
            stmt = new Statement(dest, "setLastUpdatedDate", new Object[] {lastUpdatedDate});
            stmt.execute();

            expr = new Expression(source, "isActive", new Object[0]);
            boolean isActive = (Boolean) expr.getValue();
            stmt = new Statement(dest, "setActive", new Object[] {isActive});
            stmt.execute();

            expr = new Expression(source, "isDeleted", new Object[0]);
            boolean isDeleted = (Boolean) expr.getValue();
            stmt = new Statement(dest, "setDeleted", new Object[] {isDeleted});
            stmt.execute();
        } catch(Exception ex) {
            log.error("Process Reflection failed [class-1:{}, class-2:{}], cause:{}",
                    source.getClass(), dest.getClass(), ex.getMessage());
            log.debug("Process Reflection failed [class-1:{}, class-2:{}], cause:{}",
                    source.getClass(), dest.getClass(), ex.getMessage(), ex);
            throw new RestRuntimeException(ErrorCode.UNKNOWN_ERROR, "Process reflection failed.");
        }
    }

    protected void validateRecordBeforeUpdate(Object o) {
        try {
            String className = o.getClass().getSimpleName();
            Expression expr;

            //Get ID
            expr = new Expression(o, "getId", new Object[0]);
            Long id = (Long) expr.getValue();

            //Get IS_DELETED
            expr = new Expression(o, "isDeleted", new Object[0]);
            Boolean isDeleted = (Boolean) expr.getValue();

            if (isDeleted)
                throw new RestException(ErrorCode.ALREADY_DELETED, MessageFormat.format("{0} with id:{1,number,#} already deleted", className, id));
        } catch(Exception ex) {
            throw new RestRuntimeException(ErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
        }
    }

    protected String likeSearchValue(PssFilter filter) {
        return "%" + filter.getSearch().get(PssConstant.PSS_SEARCH_VAL).toLowerCase() + "%";
    }

    protected Predicate buildPredicate(Root<?> root, CriteriaBuilder cb, QueryFilterType type, String columnName, Object paramValue) {
        String arrColums[] = columnName.split("\\|");

        List<Object> values = null;
        if(paramValue instanceof Collection<?>) {
            values = ((Collection<?>) paramValue).stream().collect(Collectors.toList());
        } else {
            values = Arrays.asList(paramValue);
        }

        if(arrColums.length != values.size()) {
            throw new RestRuntimeException(ErrorCode.INVALID_REQUEST, "Invalid filter parameter");
        }

        log.debug("Filter by Column Name {} = ({}, values.length:{})", columnName, values, values.size());
        if(values.size()==1 && StringUtils.isBlank(String.valueOf(values.get(0)))) {
            return null;
        }

        List<Predicate> predicates = new ArrayList<Predicate>();

        for(int i=0; i<arrColums.length; i++) {
            Predicate p = null;
            if((values.get(i) instanceof String && StringUtils.isNotBlank(values.get(i).toString()))) {
                String colname[] = arrColums[i].split("\\.");
                if(colname.length > 2) {
                    if(type.equals(QueryFilterType.LIKE)) {
                        p = cb.like(root.get(colname[0]).get(colname[1]).get(colname[2]), "%" + values.get(i) + "%");
                    } else if(type.equals(QueryFilterType.EQUALS_INSENSITIVE)) {
                        p = cb.equal(cb.lower(root.get(colname[0]).get(colname[1]).get(colname[2])), ((String) values.get(i)).toLowerCase());
                    } else {
                        p = cb.equal(root.get(colname[0]).get(colname[1]).get(colname[2]), values.get(i));
                    }
                } else if(colname.length > 1) {
                    if(type.equals(QueryFilterType.LIKE)) {
                        p = cb.like(root.get(colname[0]).get(colname[1]), "%" + values.get(i) + "%");
                    } else if(type.equals(QueryFilterType.EQUALS_INSENSITIVE)) {
                        p = cb.equal(cb.lower(root.get(colname[0]).get(colname[1])), ((String) values.get(i)).toLowerCase());
                    } else {
                        p = cb.equal(root.get(colname[0]).get(colname[1]), values.get(i));
                    }
                } else {
                    if(type.equals(QueryFilterType.LIKE)) {
                        p = cb.like(root.get(arrColums[i]), "%" + values.get(i) + "%");
                    } else if(type.equals(QueryFilterType.EQUALS_INSENSITIVE)) {
                        p = cb.equal(cb.lower(root.get(colname[i])), ((String) values.get(i)).toLowerCase());
                    } else {
                        p = cb.equal(root.get(arrColums[i]), values.get(i));
                    }
                }
            } else if (!CollectionUtils.isEmpty(values)){
                String colname[] = arrColums[i].split("\\.");
                if(colname.length > 2) {
                    p = cb.equal(root.get(colname[0]).get(colname[1]).get(colname[2]), values.get(i));
                } else if(colname.length > 1) {
                    p = cb.equal(root.get(colname[0]).get(colname[1]), values.get(i));
                } else {
                    p = cb.equal(root.get(arrColums[i]), values.get(i));
                }
            } else {
                log.warn("Search value for column {} is blank!", arrColums[i]);
                return null;
            }

            if(p!=null) predicates.add(p);
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

}