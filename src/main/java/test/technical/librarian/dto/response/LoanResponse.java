package test.technical.librarian.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.technical.librarian.dto.BaseDTO;
import test.technical.librarian.model.Loan;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class LoanResponse extends BaseDTO {

    private List<BookResponse> books;
    private Date dateOfLoan;
    private Date dateReturn;
    private Date dateReturned;
    private Double amountFines;
    private StudentResponse student;
    private Loan.Verification verificationLoan;
    private Loan.Verification verificationReturned;
}
