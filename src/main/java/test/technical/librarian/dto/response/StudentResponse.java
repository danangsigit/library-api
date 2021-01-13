package test.technical.librarian.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.technical.librarian.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class StudentResponse extends BaseDTO {

    private String name;
    private String nis;
    private String address;
    private String phoneNumber;
}
