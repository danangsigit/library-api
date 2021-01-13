package test.technical.librarian.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import test.technical.librarian.dto.request.StudentRequest;
import test.technical.librarian.dto.response.StudentResponse;
import test.technical.librarian.model.Student;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    StudentResponse toDto(Student e);
    Student fromDto(StudentRequest f);
    List<StudentResponse> toListDto(List<Student> list);
}
