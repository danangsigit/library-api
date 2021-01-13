package test.technical.librarian.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import test.technical.librarian.dto.request.BookRequest;
import test.technical.librarian.dto.response.BookResponse;
import test.technical.librarian.model.Book;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    BookResponse toDto(Book e);
    Book fromDto(BookRequest f);
    List<BookResponse> toListDto(List<Book> list);
}
