//package com.transport.tms.controller;
//
//import com.transport.tms.dto.request.SparePartRequest;
//import com.transport.tms.dto.response.PageResponse;
//import com.transport.tms.dto.response.SparePartResponse;
////import com.transport.tms.service.SparePartService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/spare-parts")
//@RequiredArgsConstructor
//public class SparePartController {
//
//    private final SparePartService sparePartService;
//
//    @GetMapping
//    public PageResponse<SparePartResponse> list(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        return sparePartService.list(page, size);
//    }
//
//    @GetMapping("/{id}")
//    public SparePartResponse getById(@PathVariable Long id) {
//        return sparePartService.getById(id);
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public SparePartResponse create(@Valid @RequestBody SparePartRequest request) {
//        return sparePartService.create(request);
//    }
//
//    @PutMapping("/{id}")
//    public SparePartResponse update(@PathVariable Long id, @Valid @RequestBody SparePartRequest request) {
//        return sparePartService.update(id, request);
//    }
//
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable Long id) {
//        sparePartService.delete(id);
//    }
//}
