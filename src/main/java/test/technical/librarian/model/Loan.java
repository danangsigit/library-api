package test.technical.librarian.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="loan")
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "is_deleted=false")
@ToString
public class Loan extends BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_loan", nullable = false)
    private Date dateOfLoan;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_return", nullable = false)
    private Date dateReturn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_returned")
    private Date dateReturned;

    @Column(name = "amount_fines")
    private Double amountFines;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "loan_detail",
            joinColumns = {
                    @JoinColumn(name = "id_book")},
            inverseJoinColumns = {
                    @JoinColumn(name = "id_load")}
    )
    private Set<Book> books;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student")
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_verification")
    private Verification verificationLoan;

    @Enumerated(EnumType.STRING)
    @Column(name = "returned_verification")
    private Verification verificationReturned;

    public enum Verification {
        YES("Yes"),
        NO("No");

        private String text;

        Verification(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
