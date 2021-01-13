package test.technical.librarian.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@ToString
public class PssFilter {

    private Integer draw;
    private List<HashMap<String, Object>> columns;
    private List<HashMap<String, String>> order;
    private Integer start;
    private Integer length;
    private HashMap<String, String> search;

}
