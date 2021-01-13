package test.technical.librarian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.technical.librarian.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
}
