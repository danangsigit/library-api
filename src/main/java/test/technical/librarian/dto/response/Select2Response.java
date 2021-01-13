package test.technical.librarian.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class Select2Response {
	
	private String code;
	private String message;
	private Boolean more;
	private Object results;

	@Builder
	public Select2Response(String code, String message, Boolean more, Object results) {
		this.code = code;
		this.message = message;
		this.more = more;
		this.results = results;
	}
	
}
