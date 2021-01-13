package test.technical.librarian.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.technical.librarian.dto.BaseDTO;

@Getter
@Setter
@NoArgsConstructor
public class StudentRequest extends BaseDTO {

    private String name;
    private String nis;
    private String address;
    private String phoneNumber;
}
