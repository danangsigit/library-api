package test.technical.librarian.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class GlobalFilter {
	
	private String field;
	private Object value;
	
	@Builder
	public GlobalFilter(String field, Object value) {
		this.field = field;
		this.value = value;
	}
	
}
