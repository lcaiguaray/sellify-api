package com.sellify.api.modules.catalog.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellify.api.common.exception.FieldValidationException;
import com.sellify.api.common.exception.NotFoundException;
import com.sellify.api.common.response.PageResponse;
import com.sellify.api.modules.catalog.domain.Brand;
import com.sellify.api.modules.catalog.dto.BrandRequest;
import com.sellify.api.modules.catalog.dto.BrandResponse;
import com.sellify.api.modules.catalog.mapper.BrandMapper;
import com.sellify.api.modules.catalog.repository.BrandRepository;
import com.sellify.api.modules.catalog.repository.specification.BrandSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Transactional(readOnly = true)
    public PageResponse<BrandResponse> searchByCompany(UUID companyId, String search, Boolean active, Pageable pageable) {
        
        Specification<Brand> spec = Specification
            .where(BrandSpecification.byCompanyId(companyId))
            .and(BrandSpecification.isActive(active))
            .and(BrandSpecification.search(search));

        Page<Brand> rolePage = brandRepository.findAll(spec, pageable);

        List<BrandResponse> content = rolePage.getContent().stream()
                .map(brandMapper::toResponse)
                .toList();

        return PageResponse.<BrandResponse>builder()
            .content(content)
            .pageNumber(rolePage.getNumber())
            .pageSize(rolePage.getSize())
            .totalElements(rolePage.getTotalElements())
            .totalPages(rolePage.getTotalPages())
            .isLast(rolePage.isLast())
            .build();
    }

    @Transactional(readOnly = true)
    public Brand requireActiveById(UUID id) {
        return brandRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new NotFoundException("error.brand.notfound"));
    }

    @Transactional(readOnly = true)
    public Brand requireActiveByIdAndCompany(UUID id, UUID companyId) {
        return brandRepository.findByIdAndCompanyIdAndActiveTrue(id, companyId)
            .orElseThrow(() -> new NotFoundException("error.brand.notfound"));
    }

    @Transactional(readOnly = true)
    public Brand requireByIdAndCompany(UUID id, UUID companyId) {
        return brandRepository.findByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new NotFoundException("error.brand.notfound"));
    }

    @Transactional
    public Brand create(UUID companyId, BrandRequest request) {
        if (brandRepository.existsBySlugAndCompanyId(request.slug(), companyId)) {
            throw new FieldValidationException("slug", "error.slug.duplicate");
        }

        Brand brand = new Brand();
        brand.setCompanyId(companyId);
        brand.setName(request.name());
        brand.setSlug(request.slug());
        brand.setDescription(request.description());
        brand.setActive(true);
        return brandRepository.save(brand);
    }

    @Transactional
    public Brand update(UUID id, UUID companyId, BrandRequest request) {
        Brand brand = requireByIdAndCompany(id, companyId);
        brand.setName(request.name());
        brand.setSlug(request.slug());
        brand.setDescription(request.description());
        return brand;
    }

    @Transactional
    public void setActive(UUID id, UUID companyId, boolean active) {
        Brand brand = requireByIdAndCompany(id, companyId);
        brand.setActive(active);
    }

}
