package test.technical.librarian.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.technical.librarian.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class BookResponse extends BaseDTO {

    private String title;
    private String publisher;
    private String isbn;
    private Integer total;
}
