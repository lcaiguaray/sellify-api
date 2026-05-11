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
import com.sellify.api.modules.catalog.domain.Category;
import com.sellify.api.modules.catalog.dto.CategoryRequest;
import com.sellify.api.modules.catalog.dto.CategoryResponse;
import com.sellify.api.modules.catalog.mapper.CategoryMapper;
import com.sellify.api.modules.catalog.repository.CategoryRepository;
import com.sellify.api.modules.catalog.repository.specification.CategorySpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> searchByCompany(
        UUID companyId,
        UUID parentId,
        String search,
        Boolean active,
        Pageable pageable
    ) {
        
        Specification<Category> spec = Specification
            .where(CategorySpecification.byCompanyId(companyId))
            .and(CategorySpecification.byParentId(parentId))
            .and(CategorySpecification.isActive(active))
            .and(CategorySpecification.search(search));

        Page<Category> page = categoryRepository.findAll(spec, pageable);

        List<CategoryResponse> content = page.getContent().stream()
                .map(categoryMapper::toResponse)
                .toList();

        return PageResponse.<CategoryResponse>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .isLast(page.isLast())
            .build();
    }

    @Transactional(readOnly = true)
    public Category requireActiveById(UUID id) {
        return categoryRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new NotFoundException("error.category.notfound"));
    }

    @Transactional(readOnly = true)
    public Category requireActiveByIdAndCompany(UUID id, UUID companyId) {
        return categoryRepository.findByIdAndCompanyIdAndActiveTrue(id, companyId)
            .orElseThrow(() -> new NotFoundException("error.category.notfound"));
    }

    @Transactional
    public Category create(CategoryRequest request, UUID companyId) {
        if (categoryRepository.existsBySlugAndCompanyId(request.slug(), companyId)) {
            throw new FieldValidationException("slug", "error.slug.duplicate");
        }

        Category category = new Category();
        category.setCompanyId(companyId);
        category.setParentId(request.parentId());
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
        category.setActive(true);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(UUID id, UUID companyId, CategoryRequest request) {
        Category category = requireActiveByIdAndCompany(id, companyId);
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
        return category;
    }

    @Transactional
    public void setActive(UUID id, UUID companyId, boolean active) {
        Category category = requireActiveByIdAndCompany(id, companyId);
        category.setActive(active);
    }

}
