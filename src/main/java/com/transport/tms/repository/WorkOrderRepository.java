package com.transport.tms.repository;

import com.transport.tms.domain.entity.WorkOrder;
import com.transport.tms.domain.enums.WorkOrderEntityType;
import com.transport.tms.domain.enums.WorkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    Page<WorkOrder> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<WorkOrder> findAllByStatusOrderByCreatedAtDesc(WorkOrderStatus status, Pageable pageable);
    List<WorkOrder> findAllByEntityTypeAndEntityIdOrderByCreatedAtDesc(WorkOrderEntityType entityType, Long entityId);


}
