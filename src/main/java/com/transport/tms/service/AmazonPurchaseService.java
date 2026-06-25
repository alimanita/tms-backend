package com.transport.tms.service;

import com.transport.tms.domain.entity.AmazonPurchase;
import com.transport.tms.domain.entity.AmazonPurchaseItem;
import com.transport.tms.dto.request.AmazonPurchaseRequest;
import com.transport.tms.dto.response.AmazonPurchaseResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.AmazonPurchaseMapper;
import com.transport.tms.repository.AmazonPurchaseRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AmazonPurchaseService {

    private final AmazonPurchaseRepository amazonPurchaseRepository;
    private final AmazonPurchaseMapper amazonPurchaseMapper;

    @Transactional(readOnly = true)
    public PageResponse<AmazonPurchaseResponse> list(int page, int size) {
        return PageMapper.map(
                amazonPurchaseRepository.findAllByOrderByPurchaseDateDesc(PageRequest.of(page, size)),
                amazonPurchaseMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public AmazonPurchaseResponse getById(Long id) {
        AmazonPurchase purchase = amazonPurchaseRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AmazonPurchase", id));
        return amazonPurchaseMapper.toResponse(purchase);
    }

    @Transactional
    public AmazonPurchaseResponse create(AmazonPurchaseRequest request) {
        if (amazonPurchaseRepository.existsByAmazonOrderNumber(request.amazonOrderNumber())) {
            throw new BusinessException("DUPLICATE_ORDER", "Numero de commande Amazon deja existant");
        }

        AmazonPurchase purchase = amazonPurchaseMapper.toEntity(request);
        applyDefaults(purchase);
        mapItems(request, purchase);
        recalculateAmounts(purchase);
        return amazonPurchaseMapper.toResponse(amazonPurchaseRepository.save(purchase));
    }

    @Transactional
    public AmazonPurchaseResponse update(Long id, AmazonPurchaseRequest request) {
        if (amazonPurchaseRepository.existsByAmazonOrderNumberAndIdNot(request.amazonOrderNumber(), id)) {
            throw new BusinessException("DUPLICATE_ORDER", "Numero de commande Amazon deja existant");
        }
        AmazonPurchase purchase = amazonPurchaseRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AmazonPurchase", id));
        purchase.setAmazonOrderNumber(request.amazonOrderNumber());
        purchase.setPurchaseDate(request.purchaseDate());
        purchase.setSupplier(request.supplier());
        purchase.setVatAmount(request.vatAmount());
        purchase.setAmountTtc(request.amountTtc());
        purchase.setShippingCost(request.shippingCost());
        purchase.setCurrency(request.currency());
        purchase.setStatus(request.status());
        purchase.setNotes(request.notes());
        purchase.getItems().clear();
        applyDefaults(purchase);
        mapItems(request, purchase);
        recalculateAmounts(purchase);
        return amazonPurchaseMapper.toResponse(amazonPurchaseRepository.save(purchase));
    }

    @Transactional
    public void delete(Long id) {
        AmazonPurchase purchase = amazonPurchaseRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AmazonPurchase", id));
        amazonPurchaseRepository.delete(purchase);
    }

    private void applyDefaults(AmazonPurchase purchase) {
        if (purchase.getCurrency() == null || purchase.getCurrency().isBlank()) {
            purchase.setCurrency("EUR");
        }
        if (purchase.getStatus() == null || purchase.getStatus().isBlank()) {
            purchase.setStatus("RECEIVED");
        }
        if (purchase.getShippingCost() == null) {
            purchase.setShippingCost(BigDecimal.ZERO);
        }
    }

    private void mapItems(AmazonPurchaseRequest request, AmazonPurchase purchase) {
        request.items().forEach(itemRequest -> {
            BigDecimal totalPrice = itemRequest.unitPrice()
                    .multiply(itemRequest.quantity())
                    .setScale(2, RoundingMode.HALF_UP);
            AmazonPurchaseItem item = AmazonPurchaseItem.builder()
                    .purchase(purchase)
                    .reference(itemRequest.reference())
                    .designation(itemRequest.designation())
                    .quantity(itemRequest.quantity())
                    .unitPrice(itemRequest.unitPrice())
                    .totalPrice(totalPrice)
                    .weightKg(itemRequest.weightKg())
                    .volumeM3(itemRequest.volumeM3())
                    .build();
            purchase.getItems().add(item);
        });
    }

    private void recalculateAmounts(AmazonPurchase purchase) {
        BigDecimal amountHt = purchase.getItems().stream()
                .map(AmazonPurchaseItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        purchase.setAmountHt(amountHt);

        if (purchase.getVatAmount() == null) {
            purchase.setVatAmount(amountHt.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP));
        }
        if (purchase.getAmountTtc() == null) {
            purchase.setAmountTtc(amountHt.add(purchase.getVatAmount()).setScale(2, RoundingMode.HALF_UP));
        }
    }
}
