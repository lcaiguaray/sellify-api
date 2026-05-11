package com.sellify.api.modules.catalog.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.sellify.api.common.auditing.GlobalEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variants", schema = "catalog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends GlobalEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false)
    private String name;

    @Column(name = "cost_price", precision = 15, scale = 2)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "sale_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal salePrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private Map<String, Object> attributes = new HashMap<>();

}
