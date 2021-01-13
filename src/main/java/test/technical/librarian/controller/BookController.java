package test.technical.librarian.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.technical.librarian.constant.PssConstant;
import test.technical.librarian.dto.request.BookRequest;
import test.technical.librarian.dto.request.PssFilter;
import test.technical.librarian.dto.response.BookResponse;
import test.technical.librarian.dto.response.DatatableResponse;
import test.technical.librarian.dto.response.DefaultResponse;
import test.technical.librarian.dto.response.Select2Response;
import test.technical.librarian.exception.RestRuntimeException;
import test.technical.librarian.service.BookService;
import test.technical.librarian.utils.ControllerHelper;

import java.util.List;

import static test.technical.librarian.constant.ErrorCode.SUCCESSFUL;
import static test.technical.librarian.constant.ErrorCode.UNKNOWN_ERROR;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService service;

    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public ResponseEntity<DatatableResponse> data(PssFilter filter){
        Long recordTotal = service.count(filter);
        if(filter.getDraw() == null) filter.setDraw(1);
        if(filter.getLength() == null) filter.setLength(25);
        if(filter.getStart() == null) filter.setStart(0);
        List<BookResponse> listData = service.filter(filter);
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

        BookResponse response = null;
        try {
            response = service.findOne(id).get();
        } catch(Exception e) {
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal get data buku", e);
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
            throw new RestRuntimeException(UNKNOWN_ERROR, "Gagal menghapus buku", e);
        }
        return ResponseEntity.ok(DefaultResponse.builder().code(SUCCESSFUL).message("Delete Sukses").build());
    }

    @GetMapping("/select2")
    public ResponseEntity<Select2Response> dataSelect2(@RequestParam("q") String q, @RequestParam("page") Integer page){

        PssFilter filter = ControllerHelper.buildSelect2Filter(q, page, 1, "asc");
        Long recordTotal = service.count(filter);
        List<BookResponse> listData = service.filter(filter);
        return ResponseEntity.ok(Select2Response.builder()
                .code(SUCCESSFUL).message("Sukses")
                .results(listData)
                .more((page*10) < recordTotal)
                .build());
    }

    @PostMapping("/save")
    public ResponseEntity<DefaultResponse> save(@RequestBody BookRequest dto) {
        if(dto.getId()!=null) {
            service.update(dto);
        } else {
            service.save(dto);
        }
        return ResponseEntity.ok(DefaultResponse.builder().code(SUCCESSFUL).message("Success").build());
    }
}
