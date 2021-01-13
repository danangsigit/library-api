package test.technical.librarian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.technical.librarian.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
}
