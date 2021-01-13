package test.technical.librarian.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import test.technical.librarian.dto.request.LoanRequest;
import test.technical.librarian.dto.response.LoanResponse;
import test.technical.librarian.model.Loan;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LoanMapper {

    LoanResponse toDto(Loan e);
    Loan fromDto(LoanRequest f);
    List<LoanResponse> toListDto(List<Loan> list);
}
