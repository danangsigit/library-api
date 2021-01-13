package test.technical.librarian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.technical.librarian.model.LogBook;

@Repository
public interface LogBookRepository extends JpaRepository<LogBook, String> {
}
