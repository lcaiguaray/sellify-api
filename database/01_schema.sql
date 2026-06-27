BEGIN;

-- =========================
-- 1. EXTENSIONES Y SCHEMAS
-- =========================

-- DB: sellify_db

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS people;
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS catalog;
CREATE SCHEMA IF NOT EXISTS inventory;

-- =========================
-- 2. FUNCIÓN PARA TRIGGERS DE ACTUALIZACIÓN DE FECHA
-- =========================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- =========================
-- 3. SCHEMA CORE (Estructura de Negocio)
-- =========================

CREATE TABLE core.lookup_groups (
    id TEXT PRIMARY KEY CHECK (id = UPPER(id)),
    name TEXT NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE core.lookup_values (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    lookup_group_id TEXT NOT NULL REFERENCES core.lookup_groups(id) ON UPDATE CASCADE,
    code TEXT NOT NULL CHECK (code = UPPER(code)),
    name TEXT NOT NULL,
    description TEXT,
    attributes JSONB DEFAULT '{}',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    UNIQUE(lookup_group_id, code)
);

CREATE TABLE core.companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tax_id TEXT UNIQUE NOT NULL,
    business_name TEXT NOT NULL,
    trade_name TEXT NOT NULL,
    logo_url TEXT,
    website_url TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

-- CREATE TABLE core.company_emails (
--     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--     company_id UUID REFERENCES core.companies(id),
--     type TEXT NOT NULL, -- billing, contact, support, sales, etc.
--     email TEXT NOT NULL,
--     created_at TIMESTAMPTZ DEFAULT NOW()
-- );

CREATE TABLE core.branches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES core.companies(id),
    name TEXT NOT NULL,
    address TEXT,
    phone TEXT,
    email TEXT,
    is_main BOOLEAN DEFAULT false,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    metadata JSONB DEFAULT '{}',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    UNIQUE(company_id, name),
    UNIQUE(id, company_id)
);

CREATE TABLE core.taxes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES core.companies(id),
    
    name TEXT NOT NULL,         -- Ej: 'IGV', 'IVA', 'Sales Tax'
    rate DECIMAL(7,4) NOT NULL, -- Ej: 0.1800 (para 18%), 0.1600 (para 16%)
    
    is_default BOOLEAN DEFAULT false,
    active BOOLEAN DEFAULT true,
    
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    UNIQUE(company_id, name)
);

-- =========================
-- 4. SCHEMA PEOPLE (Identidades)
-- =========================

CREATE TABLE people.identities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_type_id UUID NOT NULL REFERENCES core.lookup_values(id),
    is_legal_entity BOOLEAN DEFAULT false,
    tax_id TEXT UNIQUE NOT NULL,
    first_name TEXT,
    last_name TEXT,
    business_name TEXT,
    trade_name TEXT,
    gender_id UUID REFERENCES core.lookup_values(id),
    civil_status_id UUID REFERENCES core.lookup_values(id),
    education_level_id UUID REFERENCES core.lookup_values(id),
    email TEXT,
    phone TEXT,
    inception_date DATE,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE TABLE people.employees (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    identity_id UUID NOT NULL REFERENCES people.identities(id),
    company_id UUID NOT NULL REFERENCES core.companies(id),
    branch_id UUID NOT NULL REFERENCES core.branches(id),
    code TEXT NOT NULL,

    -- Puesto (opcional)
    -- job_title_id UUID REFERENCES core.lookup_values(id),

    salary DECIMAL(15,2) CHECK (salary >= 0),
    hire_date DATE NOT NULL DEFAULT CURRENT_DATE,
    termination_date DATE,
    active BOOLEAN NOT NULL DEFAULT true,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,

    UNIQUE(identity_id, company_id),
    UNIQUE(company_id, code),

    CONSTRAINT fk_branch_belongs_to_company
        FOREIGN KEY (branch_id, company_id)
        REFERENCES core.branches(id, company_id)
        DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE people.customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    identity_id UUID NOT NULL REFERENCES people.identities(id),
    company_id UUID NOT NULL REFERENCES core.companies(id),

    credit_limit DECIMAL(15,2) DEFAULT 0 CHECK (credit_limit >= 0),
    is_vip BOOLEAN DEFAULT false,

    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,

    UNIQUE(identity_id, company_id)
);

CREATE TABLE people.suppliers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    identity_id UUID NOT NULL REFERENCES people.identities(id),
    company_id UUID NOT NULL REFERENCES core.companies(id),

    bank_account TEXT,
    payment_terms_days INT DEFAULT 30 CHECK (payment_terms_days >= 0),

    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,

    UNIQUE(identity_id, company_id)
);

-- =========================
-- 4. SCHEMA CATALOG (Catálogo comercial)
-- =========================

CREATE TABLE catalog.brands (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES core.companies(id),
    name TEXT NOT NULL,
    slug TEXT NOT NULL,
    description TEXT,
    logo_url TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    UNIQUE(company_id, slug)
);

CREATE TABLE catalog.categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES core.companies(id),
    parent_id UUID REFERENCES catalog.categories(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    slug TEXT NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    UNIQUE(company_id, slug)
);

CREATE TABLE catalog.products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES core.companies(id),
    category_id UUID NOT NULL REFERENCES catalog.categories(id),
    brand_id UUID REFERENCES catalog.brands(id),
    
    -- uom_id: Unit of Measure (Ej: Unidades, Litros, Kg)
    uom_id UUID NOT NULL REFERENCES core.lookup_values(id), 
    product_type_id UUID REFERENCES core.lookup_values(id),
    
    name TEXT NOT NULL,
    slug TEXT NOT NULL,
    description TEXT,
    
    has_variant BOOLEAN DEFAULT false,
    is_taxable BOOLEAN DEFAULT true,
    active BOOLEAN DEFAULT true,
    
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    UNIQUE(company_id, slug)
);

CREATE TABLE catalog.product_variants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES catalog.products(id) ON DELETE CASCADE,
    
    sku TEXT NOT NULL,
    barcode TEXT,
    name TEXT, -- Ej: "Camiseta Roja - Talla M" (Opcional, si es diferente al producto base)
    
    cost_price DECIMAL(15,2) DEFAULT 0 CHECK (cost_price >= 0),
    sale_price DECIMAL(15,2) NOT NULL CHECK (sale_price >= 0),
    
    attributes JSONB DEFAULT '{}', -- Ideal para variaciones dinámicas (color, talla, peso)
    
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    UNIQUE(product_id, sku)
);

CREATE TABLE catalog.product_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES catalog.products(id) ON DELETE CASCADE,
    product_variant_id UUID NOT NULL REFERENCES catalog.product_variants(id) ON DELETE CASCADE,
    
    url TEXT NOT NULL,
    is_primary BOOLEAN DEFAULT false,
    sort_order INTEGER DEFAULT 0,
    
    alt_text TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- =========================
-- 5. SCHEMA AUTH (Seguridad)
-- =========================

CREATE TABLE auth.roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    company_id UUID NOT NULL REFERENCES core.companies(id),
    name TEXT NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    UNIQUE(company_id, name)
);

-- Tabla de permisos usando el CÓDIGO como PRIMARY KEY
CREATE TABLE auth.permissions (
    id TEXT PRIMARY KEY CHECK (id = UPPER(id)),
    module TEXT NOT NULL,
    level TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    active BOOLEAN DEFAULT true
);

CREATE TABLE auth.role_permissions (
    role_id UUID REFERENCES auth.roles(id) ON DELETE CASCADE,
    permission_id TEXT REFERENCES auth.permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE auth.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    identity_id UUID UNIQUE REFERENCES people.identities(id),
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    last_login_at TIMESTAMPTZ,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE TABLE auth.user_roles (
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    role_id UUID REFERENCES auth.roles(id) ON DELETE CASCADE,
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE auth.user_companies (
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    company_id UUID NOT NULL REFERENCES core.companies(id) ON DELETE CASCADE,
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    created_by UUID,
    PRIMARY KEY (user_id, company_id)
);

CREATE TABLE auth.sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    company_id UUID NOT NULL REFERENCES core.companies(id) ON DELETE CASCADE,

    token TEXT NOT NULL UNIQUE,

    ip_address TEXT,
    user_agent TEXT NOT NULL,

    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL,

    revoked BOOLEAN DEFAULT false,

    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE (user_id, company_id, token)
);

-- =========================
-- 6. ALTERS
-- =========================

---
-- CORE
---

ALTER TABLE core.lookup_values ADD CONSTRAINT fk_core_val_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE core.lookup_values ADD CONSTRAINT fk_core_val_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

ALTER TABLE core.companies ADD CONSTRAINT fk_comp_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE core.companies ADD CONSTRAINT fk_comp_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

ALTER TABLE core.branches ADD CONSTRAINT fk_bran_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE core.branches ADD CONSTRAINT fk_bran_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_lookup_groups_active_true ON core.lookup_groups (id) WHERE active = true;

CREATE INDEX IF NOT EXISTS idx_lookup_values_group ON core.lookup_values (lookup_group_id);
CREATE INDEX IF NOT EXISTS idx_lookup_values_code ON core.lookup_values (code);
CREATE INDEX IF NOT EXISTS idx_lookup_values_group_active_sort ON core.lookup_values (lookup_group_id, name) WHERE active = true;

CREATE INDEX IF NOT EXISTS idx_branches_company_main_active ON core.branches (company_id) WHERE is_main = true AND active = true;
CREATE INDEX IF NOT EXISTS idx_branches_metadata_gin ON core.branches USING GIN (metadata);

ALTER TABLE core.taxes ADD CONSTRAINT fk_taxes_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE core.taxes ADD CONSTRAINT fk_taxes_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

---
-- PEOPLE
---

ALTER TABLE people.identities ADD CONSTRAINT fk_iden_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE people.identities ADD CONSTRAINT fk_iden_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

ALTER TABLE people.employees ADD CONSTRAINT fk_empl_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE people.employees ADD CONSTRAINT fk_empl_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

ALTER TABLE people.customers ADD CONSTRAINT fk_cust_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE people.customers ADD CONSTRAINT fk_cust_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

ALTER TABLE people.suppliers ADD CONSTRAINT fk_supp_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE people.suppliers ADD CONSTRAINT fk_supp_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_identities_document_type ON people.identities (document_type_id);
CREATE INDEX IF NOT EXISTS idx_identities_gender ON people.identities (gender_id);
CREATE INDEX IF NOT EXISTS idx_identities_civil_status ON people.identities (civil_status_id);
CREATE INDEX IF NOT EXISTS idx_identities_education_level ON people.identities (education_level_id);

CREATE INDEX IF NOT EXISTS idx_employees_branch ON people.employees (branch_id);
CREATE INDEX IF NOT EXISTS idx_employees_active_true ON people.employees (identity_id) WHERE active = true;

CREATE INDEX IF NOT EXISTS idx_customers_active_true ON people.customers (identity_id) WHERE active = true;

CREATE INDEX IF NOT EXISTS idx_suppliers_active_true ON people.suppliers (identity_id) WHERE active = true;

---
-- CATALOG
---

ALTER TABLE catalog.brands ADD CONSTRAINT fk_brands_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE catalog.brands ADD CONSTRAINT fk_brands_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

ALTER TABLE catalog.categories ADD CONSTRAINT fk_categories_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE catalog.categories ADD CONSTRAINT fk_categories_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

ALTER TABLE catalog.products ADD CONSTRAINT fk_products_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE catalog.products ADD CONSTRAINT fk_products_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

ALTER TABLE catalog.product_variants ADD CONSTRAINT fk_variants_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE catalog.product_variants ADD CONSTRAINT fk_variants_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_brands_company_active ON catalog.brands (company_id) WHERE active = true;

CREATE INDEX IF NOT EXISTS idx_categories_company_parent ON catalog.categories (company_id, parent_id) WHERE active = true;

CREATE INDEX IF NOT EXISTS idx_products_company_category ON catalog.products (company_id, category_id) WHERE active = true;
CREATE INDEX IF NOT EXISTS idx_products_type ON catalog.products (product_type_id) WHERE active = true;

CREATE INDEX IF NOT EXISTS idx_variants_product_active ON catalog.product_variants (product_id) WHERE active = true;
CREATE INDEX IF NOT EXISTS idx_variants_sku ON catalog.product_variants (sku);
CREATE INDEX IF NOT EXISTS idx_variants_attributes_gin ON catalog.product_variants USING GIN (attributes);

CREATE INDEX IF NOT EXISTS idx_product_images_product ON catalog.product_images(product_id);
CREATE INDEX IF NOT EXISTS idx_product_images_variant ON catalog.product_images(product_variant_id);

---
-- AUTH
---
ALTER TABLE auth.roles ADD CONSTRAINT fk_roles_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE auth.roles ADD CONSTRAINT fk_roles_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

ALTER TABLE auth.users ADD CONSTRAINT fk_users_creator FOREIGN KEY (created_by) REFERENCES auth.users(id) ON DELETE SET NULL;
ALTER TABLE auth.users ADD CONSTRAINT fk_users_editor FOREIGN KEY (updated_by) REFERENCES auth.users(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_role_permissions_permission ON auth.role_permissions (permission_id);

CREATE INDEX IF NOT EXISTS idx_user_roles_role ON auth.user_roles (role_id);

CREATE INDEX IF NOT EXISTS idx_users_active_true ON auth.users (id) WHERE active = true;

CREATE INDEX IF NOT EXISTS idx_user_companies_user_company ON auth.user_companies (user_id, company_id);

CREATE INDEX IF NOT EXISTS idx_sessions_user_company ON auth.sessions (user_id, company_id);

CREATE INDEX IF NOT EXISTS idx_sessions_expires_active ON auth.sessions (expires_at) WHERE revoked = false;

-- =========================
-- 7. ASIGNACIÓN DE TRIGGERS
-- =========================
CREATE TRIGGER tr_upd_lookup_groups BEFORE UPDATE ON core.lookup_groups FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_lookup_values BEFORE UPDATE ON core.lookup_values FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_companies BEFORE UPDATE ON core.companies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_branches BEFORE UPDATE ON core.branches FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_identities BEFORE UPDATE ON people.identities FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_employees BEFORE UPDATE ON people.employees FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_customers BEFORE UPDATE ON people.customers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_suppliers BEFORE UPDATE ON people.suppliers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_roles BEFORE UPDATE ON auth.roles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_users BEFORE UPDATE ON auth.users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_sessions BEFORE UPDATE ON auth.sessions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_brands BEFORE UPDATE ON catalog.brands FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_categories BEFORE UPDATE ON catalog.categories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_products BEFORE UPDATE ON catalog.products FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_product_variants BEFORE UPDATE ON catalog.product_variants FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_upd_taxes BEFORE UPDATE ON core.taxes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

COMMIT;
