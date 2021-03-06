package test.technical.librarian.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.technical.librarian.constant.PssConstant;
import test.technical.librarian.dto.request.PssFilter;
import test.technical.librarian.dto.request.StudentRequest;
import test.technical.librarian.dto.response.*;
import test.technical.librarian.exception.RestRuntimeException;
import test.technical.librarian.model.Student;
import test.technical.librarian.service.StudentService;
import test.technical.librarian.utils.ControllerHelper;

import java.util.List;
import java.util.Optional;

import static test.technical.librarian.constant.ErrorCode.SUCCESSFUL;
import static test.technical.librarian.constant.ErrorCode.UNKNOWN_ERROR;

@RestController
@RequestMapping("/api/student")
@Slf4j
public class StudentController {
    private final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public ResponseEntity<DatatableResponse> data(PssFilter filter){
        Long recordTotal = service.count(filter);
        if(filter.getDraw() == null) filter.setDraw(1);
        if(filter.getLength() == null) filter.setLength(25);
        if(filter.getStart() == null) filter.setStart(0);
        List<StudentResponse> listData = service.filter(filter);
        return ResponseEntity.ok(DatatableResponse.builder()
                .code(SUCCESSFUL).message("Sukses").draw(filter.getDraw())
                .data(listData)
                .recordsFiltered(recordTotal)
                .recordsTotal(recordTotal)
                .search(
                        (filter.getSearch() != null ? filter.getSearch().get(PssConstant.PSS_SEARCH_VAL) : null)
                )
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultResponse> get(@PathVariable String id){

        StudentResponse response = null;
        try {
            Optional<StudentResponse> student = service.findOne(id);
            if(student.isPresent())
            response = student.get();
        } catch(Exception e) {
            log.error("Gagal get data Student {}", e.getMessage());
        }
        return ResponseEntity.ok(DefaultResponse.builder().data(response).code(SUCCESSFUL).message("get data Sukses").build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DefaultResponse> delete(@PathVariable String id){
        try {
            service.delete(id);
        } catch(DataIntegrityViolationException e) {
            service.setDelete(id);
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal menghapus Student", e);
        }
        return ResponseEntity.ok(DefaultResponse.builder().code(SUCCESSFUL).message("Delete Sukses").build());
    }

    @GetMapping("/select2")
    public ResponseEntity<Select2Response> dataSelect2(@RequestParam("q") String q, @RequestParam("page") Integer page){

        PssFilter filter = ControllerHelper.buildSelect2Filter(q, page, 1, "asc");
        Long recordTotal = service.count(filter);
        List<StudentResponse> listData = service.filter(filter);
        return ResponseEntity.ok(Select2Response.builder()
                .code(SUCCESSFUL).message("Sukses")
                .results(listData)
                .more((page*10) < recordTotal)
                .build());
    }

    @PostMapping("/save")
    public ResponseEntity<DefaultResponse> save(@RequestBody StudentRequest dto) {
        StudentResponse response = null;
        if(dto.getId()!=null) {
            response = service.update(dto).get();
        } else {
            response = service.save(dto);
        }
        return ResponseEntity.ok(DefaultResponse.builder().code(SUCCESSFUL).data(response).message("Success").build());
    }
}
