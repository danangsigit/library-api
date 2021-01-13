package test.technical.librarian.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DatatableResponse {
	
	private String code;
	private String message;
	private Integer draw;
	private Long recordsTotal;
	private Long recordsFiltered;
	private String search;
	private Object data;
	
	@Builder
	public DatatableResponse(String code, String message, Integer draw, Long recordsTotal, Long recordsFiltered,
                             String search, Object data) {
		this.code = code;
		this.message = message;
		this.draw = draw;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.search = search;
		this.data = data;
	}
	
}
