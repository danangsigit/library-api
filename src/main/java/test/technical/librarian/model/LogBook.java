package test.technical.librarian.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="log_book")
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "is_deleted=false")
@ToString
public class LogBook extends BaseEntity {

    @Column(name = "id_book")
    private String idBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_book", updatable = false, insertable = false)
    private Book book;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_log", nullable = false)
    private Date dateLog;

    @Column(columnDefinition="TEXT")
    private String description;

    @Column(length = 5)
    private Integer in;

    @Column(length = 5)
    private Integer out;

    @Column(length = 5)
    private Integer balance;

    @Column(length = 4)
    private Integer year;
}
