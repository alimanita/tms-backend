//package com.transport.tms.controller;
//
//import com.transport.tms.dto.request.TireAssignmentRequest;
//import com.transport.tms.dto.request.TireRequest;
//import com.transport.tms.dto.response.PageResponse;
//import com.transport.tms.dto.response.TireAssignmentResponse;
//import com.transport.tms.dto.response.TireResponse;
//import com.transport.tms.service.TireService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/tires")
//@RequiredArgsConstructor
//public class TireController {
//
//    private final TireService tireService;
//
//
//    @GetMapping
//    public PageResponse<TireResponse> list(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ) {
//        return tireService.list(page, size);
//    }
//
//    @GetMapping("/{id}")
//    public TireResponse getById(@PathVariable Long id) {
//        return tireService.getById(id);
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public TireResponse create(@Valid @RequestBody TireRequest request) {
//        return tireService.create(request);
//    }
//
//    @PutMapping("/{id}")
//    public TireResponse update(@PathVariable Long id, @Valid @RequestBody TireRequest request) {
//        return tireService.update(id, request);
//    }
//
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable Long id) {
//        tireService.delete(id);
//    }
//
//    // Assignments endpoints
//    @GetMapping("/assignments")
//    public PageResponse<TireAssignmentResponse> listAssignments(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ) {
//        return assignmentService.list(page, size);
//    }
//
//    @GetMapping("/assignments/vehicle/{vehicleId}")
//    public List<TireAssignmentResponse> getAssignmentsByVehicle(@PathVariable Long vehicleId) {
//        return assignmentService.getByVehicle(vehicleId);
//    }
//
//    @GetMapping("/assignments/tire/{tireId}")
//    public List<TireAssignmentResponse> getAssignmentsByTire(@PathVariable Long tireId) {
//        return assignmentService.getByTire(tireId);
//    }
//
//    @PostMapping("/assignments")
//    @ResponseStatus(HttpStatus.CREATED)
//    public TireAssignmentResponse assignTire(@Valid @RequestBody TireAssignmentRequest request) {
//        return assignmentService.create(request);
//    }
//
//    @PutMapping("/assignments/{id}/unmount")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void unmountTire(@PathVariable Long id, @Valid @RequestBody TireAssignmentRequest request) {
//        assignmentService.unmount(id, request);
//    }
//}
