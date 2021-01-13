package test.technical.librarian.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.technical.librarian.dto.BaseDTO;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class LogBookResponse extends BaseDTO {

    private BookResponse idBook;
    private Date dateLog;
    private String description;
    private Integer in;
    private Integer out;
    private Integer balance;
    private Integer year;
}
