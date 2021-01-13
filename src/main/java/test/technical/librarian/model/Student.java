package test.technical.librarian.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="student")
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "is_deleted=false")
@ToString
public class Student extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nis", nullable = false)
    private String nis;

    @Column(name = "nis", columnDefinition="TEXT")
    private String address;

    @Column(length = 15, name = "phone_number")
    private String phoneNumber;
}
