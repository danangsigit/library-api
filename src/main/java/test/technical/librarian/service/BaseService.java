package test.technical.librarian.service;

import test.technical.librarian.dto.request.PssFilter;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseService<T extends Object, ID extends Serializable, R extends Object> {

    T save(R s);

    Optional<T> update(R s);

    Optional<T> findOne(ID id);

    List<T> filter(PssFilter filter);

    Long count(PssFilter filter);

    void delete(ID id);

    void setDelete(ID id);

    void setActive(ID id, Boolean active);
}
