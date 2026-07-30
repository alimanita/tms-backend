package com.transport.tms.service;



import com.transport.tms.dto.RolesDto;

import java.util.List;

public interface RolesService {

    RolesDto save(RolesDto dto);

    RolesDto findById(Long id);

    List<RolesDto> findAll();

    void delete(Long id);
}