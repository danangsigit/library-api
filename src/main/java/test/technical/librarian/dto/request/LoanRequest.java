package test.technical.librarian.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.technical.librarian.dto.BaseDTO;
import test.technical.librarian.model.Loan;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoanRequest extends BaseDTO {

    private List<String> books;
    private Date dateOfLoan;
    private Date dateReturn;
    private Date dateReturned;
    private Double amountFines;
    private String student;
    private Loan.Verification verificationLoan;
    private Loan.Verification verificationReturned;
}
