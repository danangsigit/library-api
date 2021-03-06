package test.technical.librarian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.technical.librarian.model.Book;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    Set<Book> findByIdIn(List<String> ids);
}
