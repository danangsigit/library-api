package test.technical.librarian.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseDTO implements Serializable {
    private static final long serialVersionUID = -2581206638402708634L;
    private String id;
    private boolean active = true;
}
