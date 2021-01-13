package test.technical.librarian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.technical.librarian.model.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {
}
