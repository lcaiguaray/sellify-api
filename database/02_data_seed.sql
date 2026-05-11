BEGIN;

------------------------------------------------------------
-- LOOKUP GROUPS
------------------------------------------------------------
insert into core.lookup_groups (id, name, description) values
('DOCUMENT_TYPE', 'Tipos de Documento', 'Documentos de identidad legal'),
('GENDER', 'Géneros', 'Identificación de género'),
('CIVIL_STATUS', 'Estado Civil', 'Situación legal de convivencia'),
('EDUCATION_LEVEL', 'Nivel de Educación', 'Grado académico'),
('UOM', 'Unidades de Medida', 'Catálogo estándar de unidades físicas, peso y volumen para el inventario y facturación'),
('PRODUCT_TYPE', 'Tipos de Producto', 'Clasificación de ítems según su naturaleza física o intangible (Relevante para SUNAT e inventario)')
on conflict (id) do nothing;

------------------------------------------------------------
-- LOOKUP values
------------------------------------------------------------
insert into core.lookup_values (lookup_group_id, code, name, description, attributes) values
('DOCUMENT_TYPE', 'DNI', 'Documento Nacional de Identidad', null, '{"exact_length": 8, "requires_tax_validation": false}'::jsonb),
('DOCUMENT_TYPE', 'RUC', 'Registro Único de Contribuyentes', null, '{"exact_length": 11, "requires_tax_validation": true}'::jsonb),
('DOCUMENT_TYPE', 'PASSPORT', 'Pasaporte', null, '{"exact_length": null, "requires_tax_validation": false}'::jsonb),
('DOCUMENT_TYPE', 'CE', 'Carné de Extranjería', null, '{"exact_length": null, "requires_tax_validation": false}'::jsonb),

('GENDER', 'MALE', 'Masculino', null, null),
('GENDER', 'FEMALE', 'Femenino', null, null),
('GENDER', 'OTHER', 'Otro', null, null),

('CIVIL_STATUS', 'SINGLE', 'Soltero(a)', null, null),
('CIVIL_STATUS', 'MARRIED', 'Casado(a)', null, null),

('EDUCATION_LEVEL', 'UNIVERSITY', 'Universitario', null, null),
('EDUCATION_LEVEL', 'POSTGRADUATE', 'Postgrado', null, null),

('UOM', 'NIU', 'Unidad / Pieza', 'Unidad estándar de venta por pieza indivisible', '{"tax_code": "NIU", "symbol": "Und."}'::jsonb),
('UOM', 'BX', 'Caja', 'Caja o empaque cerrado con múltiples unidades', '{"tax_code": "BX", "symbol": "Caja"}'::jsonb),
('UOM', 'PK', 'Paquete', 'Paquete promocional o bundle', '{"tax_code": "PK", "symbol": "Pqt."}'::jsonb),
('UOM', 'KGM', 'Kilogramo', 'Unidad base de masa', '{"tax_code": "KGM", "symbol": "kg"}'::jsonb),
('UOM', 'GRM', 'Gramo', 'Fracción de masa para productos pequeños', '{"tax_code": "GRM", "symbol": "g"}'::jsonb),
('UOM', 'LTR', 'Litro', 'Unidad de medida para líquidos', '{"tax_code": "LTR", "symbol": "L"}'::jsonb),
('UOM', 'MTR', 'Metro', 'Unidad de medida para cables, telas, etc.', '{"tax_code": "MTR", "symbol": "m"}'::jsonb),

('PRODUCT_TYPE', 'BIEN_FISICO', 'Bien Físico', 'Bienes corporales que requieren almacenamiento y traslado físico.', '{"track_inventory": true, "requires_shipping": true, "requires_guia_remision": true, "sunat_type": "BIEN"}'::jsonb),
('PRODUCT_TYPE', 'SERVICIO', 'Servicio', 'Prestación de servicios intangibles. No mueve inventario.', '{"track_inventory": false, "requires_shipping": false, "requires_guia_remision": false, "sunat_type": "SERVICIO", "check_detraction": true}'::jsonb),
('PRODUCT_TYPE', 'BIEN_DIGITAL', 'Bien Digital', 'Archivos o accesos digitales. Se entregan online.', '{"track_inventory": false, "requires_shipping": false, "requires_guia_remision": false, "sunat_type": "BIEN", "requires_download_link": true}'::jsonb)
on conflict do nothing;

------------------------------------------------------------
-- PERMISSIONS
------------------------------------------------------------
insert into auth.permissions (id, name, description) values

-- Lookup Group
('LOOKUP_GROUP.READ', 'Grupos de Catálogos - Lectura', 'Permite ver y listar los grupos de catálogos o listas del sistema.'),
-- ('LOOKUP_GROUP.WRITE', 'Grupos de Catálogos - Escritura', 'Permite crear y editar la información de los grupos de catálogos.'),
-- ('LOOKUP_GROUP.MANAGE', 'Grupos de Catálogos - Administración', 'Permite administrar grupos de catálogos: habilitar, deshabilitar o eliminarlos.'),

-- Lookup Value
('LOOKUP_VALUE.READ', 'Valores de Catálogo - Lectura', 'Permite ver y listar los valores específicos dentro de un grupo de catálogo.'),
('LOOKUP_VALUE.WRITE', 'Valores de Catálogo - Escritura', 'Permite agregar y editar los valores o elementos pertenecientes a un catálogo.'),
('LOOKUP_VALUE.MANAGE', 'Valores de Catálogo - Administración', 'Permite administrar los valores de un catálogo: habilitar, deshabilitar, eliminar o cambiar su orden de visualización.'),

-- User
('USER.READ', 'Usuarios - Lectura', 'Permite ver y listar usuarios del sistema.'),
('USER.WRITE', 'Usuarios - Escritura', 'Permite crear y editar información de usuarios.'),
('USER.MANAGE', 'Usuarios - Administración', 'Permite administrar usuarios: habilitar, deshabilitar, eliminar o restablecer credenciales.'),

-- Role
('ROLE.READ', 'Roles - Lectura', 'Permite ver y listar roles del sistema.'),
('ROLE.WRITE', 'Roles - Escritura', 'Permite crear y editar roles.'),
('ROLE.MANAGE', 'Roles - Administración', 'Permite administrar roles: habilitar, deshabilitar, eliminar y asignar permisos.'),

-- Products
('PRODUCTS.READ', 'Productos - Lectura', 'Permite ver y listar productos.'),
('PRODUCTS.WRITE', 'Productos - Escritura', 'Permite crear y editar productos.'),
('PRODUCTS.MANAGE', 'Productos - Administración', 'Permite administrar productos: publicar, despublicar o realizar acciones sensibles.'),

-- Orders
('ORDERS.READ', 'Órdenes - Lectura', 'Permite ver y listar órdenes.'),
('ORDERS.MANAGE', 'Órdenes - Administración', 'Permite administrar órdenes: cancelar, cerrar o forzar cambios de estado.'),

-- Settings
('SETTINGS.MANAGE', 'Configuración - Administración', 'Permite administrar la configuración general del sistema.')
on conflict do nothing;

------------------------------------------------------------
-- COMPANIES
------------------------------------------------------------
insert into core.companies (tax_id, business_name, trade_name, website_url)
values
('20123456789', 'Sellify Technologies S.A.C.', 'Sellify', 'https://sellify.pe'),
('20654321987', 'Andes Retail Group S.A.C.', 'Andes Retail', 'https://andesretail.pe')
on conflict (tax_id) do nothing;

------------------------------------------------------------
-- TAXES
------------------------------------------------------------

insert into core.taxes (company_id, name, rate, is_default)
select c.id, t.name, t.rate, t.is_default
from core.companies c
cross join (
  values
  ('IGV 18%', 0.1800, true),
  ('IGV 16%', 0.1600, false)
) t(name, rate, is_default)
on conflict (company_id, name) do nothing;

------------------------------------------------------------
-- BRANDS
------------------------------------------------------------

insert into catalog.brands (company_id, name, slug, description)
select c.id, t.name, t.slug, t.description
from core.companies c
cross join (
  values
  ('Apple', 'apple', 'Empresa tecnológica multinacional.'),
  ('Samsung', 'samsung', 'Conglomerado de empresas multinacionales.'),
  ('Nike', 'nike', 'Equipamiento deportivo, ropa y calzado.')
) t(name, slug, description)
on conflict (company_id, slug) do nothing;

------------------------------------------------------------
-- CATEGORIES
------------------------------------------------------------

insert into catalog.categories (company_id, name, slug, description)
select c.id, t.name, t.slug, t.description
from core.companies c
cross join (
  values
  ('Electrónica', 'electronica', 'Dispositivos y gadgets tecnológicos'),
  ('Ropa y Moda', 'ropa-y-moda', 'Indumentaria para todas las edades')
) t(name, slug, description)
on conflict (company_id, slug) do nothing;

------------------------------------------------------------
-- SUB CATEGORIES
------------------------------------------------------------

insert into catalog.categories (company_id, parent_id, name, slug, description)
select c.company_id, c.id, t.name, t.slug, t.description
from catalog.categories c
cross join (
  values
  ('Smartphones', 'smartphones', 'Teléfonos móviles inteligentes'),
  ('Laptops', 'laptops', 'Computadoras portátiles'),
  ('Camisetas', 'camisetas', 'Camisetas de algodón y deportivas')
) t(name, slug, description)
on conflict (company_id, slug) do nothing;

------------------------------------------------------------
-- PRODUCTS
------------------------------------------------------------
insert into catalog.products (company_id, category_id, brand_id, uom_id, name, slug, description, is_taxable)
select c.id, t.category_id, t.brand_id, t.uom_id, t.name, t.slug, t.description, t.is_taxable
from core.companies c
cross join lateral (
  values
  (
    (select id from catalog.categories where slug = 'smartphones' and company_id = c.id),
    (select id from catalog.brands where slug = 'apple' and company_id = c.id),
    (select id from core.lookup_values where code = 'NIU'),
    'iPhone 15 Pro Max',
    'iphone-15-pro-max',
    'El iPhone más avanzado con chip A17 Pro y diseño de titanio aeroespacial.',
    true
  ),
  (
    (select id from catalog.categories where slug = 'smartphones' and company_id = c.id),
    (select id from catalog.brands where slug = 'samsung' and company_id = c.id),
    (select id from core.lookup_values where code = 'NIU'),
    'Galaxy S24 Ultra',
    'galaxy-s24-ultra',
    'Inteligencia artificial integrada, cámara de 200MP y S Pen.',
    true
  ),
  (
    (select id from catalog.categories where slug = 'camisetas' and company_id = c.id),
    (select id from catalog.brands where slug = 'nike' and company_id = c.id),
    (select id from core.lookup_values where code = 'NIU'),
    'Camiseta Nike Dri-FIT',
    'camiseta-nike-dri-fit',
    'Tecnología de capilarización del sudor para mantener la transpirabilidad y la comodidad.',
    true
  )
) t(category_id, brand_id, uom_id, name, slug, description, is_taxable)
on conflict (company_id, slug) do nothing;

------------------------------------------------------------
-- VARIANTS
------------------------------------------------------------

insert into catalog.product_variants (product_id, sku, barcode, name, cost_price, sale_price, attributes)
select p.id, t.sku, t.barcode, t.name, t.cost_price, t.sale_price, t.attributes
from catalog.products p
cross join (
  values
  ('apl-ip15pm-nat-256', '195949041111', 'iPhone 15 Pro Max - Titanio Natural 256GB', 950.00, 1199.00, '{"color": "Titanio Natural", "almacenamiento": "256GB"}'::jsonb),
  ('apl-ip15pm-blk-256', '195949041222', 'iPhone 15 Pro Max - Titanio Negro 256GB', 950.00, 1199.00, '{"color": "Titanio Negro", "almacenamiento": "256GB"}'::jsonb),
  ('apl-ip15pm-net-512', '195949041333', 'iPhone 15 Pro Max - Titanio Natural 512GB', 1050.00, 1399.00, '{"color": "Titanio Natural", "almacenamiento": "512GB"}'::jsonb)
) t(sku, barcode, name, cost_price, sale_price, attributes)
where p.slug = 'iphone-15-pro-max'
on conflict (product_id, sku) do nothing;

insert into catalog.product_variants (product_id, sku, barcode, name, cost_price, sale_price, attributes)
select p.id, t.sku, t.barcode, t.name, t.cost_price, t.sale_price, t.attributes
from catalog.products p
cross join (
  values
  ('sam-s24u-gry-512', '880609531111', 'Galaxy S24 Ultra - Gris Titanio 512GB', 980.00, 1299.00, '{"color": "Gris Titanio", "almacenamiento": "512GB"}'::jsonb),
  ('sam-s24u-vio-512', '880609532222', 'Galaxy S24 Ultra - Violeta Titanio 512GB', 980.00, 1299.00, '{"color": "Violeta Titanio", "almacenamiento": "512GB"}'::jsonb)
) t(sku, barcode, name, cost_price, sale_price, attributes)
where p.slug = 'galaxy-s24-ultra'
on conflict (product_id, sku) do nothing;

insert into catalog.product_variants (product_id, sku, barcode, name, cost_price, sale_price, attributes)
select p.id, t.sku, t.barcode, t.name, t.cost_price, t.sale_price, t.attributes
from catalog.products p
cross join (
  values
  ('nk-dfit-blk-m', '193151000111', 'Camiseta Nike Dri-FIT - Negro Talla M', 12.00, 35.00, '{"color": "Negro", "talla": "M", "material": "Poliéster"}'::jsonb),
  ('nk-dfit-blk-l', '193151000222', 'Camiseta Nike Dri-FIT - Negro Talla L', 12.00, 35.00, '{"color": "Negro", "talla": "L", "material": "Poliéster"}'::jsonb),
  ('nk-dfit-red-m', '193151000333', 'Camiseta Nike Dri-FIT - Rojo Talla M', 12.00, 35.00, '{"color": "Rojo", "talla": "M", "material": "Poliéster"}'::jsonb)
) t(sku, barcode, name, cost_price, sale_price, attributes)
where p.slug = 'camiseta-nike-dri-fit'
on conflict (product_id, sku) do nothing;

------------------------------------------------------------
-- ROLES POR EMPRESA
------------------------------------------------------------
insert into auth.roles (company_id, name, description)
select c.id, r.name, r.description
from core.companies c
cross join (
 values
 ('Super Administrator', 'Full system administrator'),
 ('Administrator', 'Business administrator'),
 ('Seller', 'Product manager')
) r(name, description)
on conflict (company_id, name) do nothing;

------------------------------------------------------------
-- ROLE PERMISSIONS
------------------------------------------------------------
insert into auth.role_permissions (role_id, permission_id)
select r.id, p.id
from auth.roles r
join auth.permissions p ON TRUE
where r.name = 'Super Administrator'
on conflict do nothing;

insert into auth.role_permissions (role_id, permission_id)
select r.id, p.id
from auth.roles r
join auth.permissions p
  ON p.id != 'SETTINGS.MANAGE'
where r.name = 'Administrator'
on conflict do nothing;

------------------------------------------------------------
-- IDENTITIES
------------------------------------------------------------
insert into people.identities (document_type_id, tax_id, first_name, last_name, gender_id, email)
values
(
  (select id from core.lookup_values where code = 'DNI'),
  '12345678',
  'Luis',
  'Caiguaray',
  (select id from core.lookup_values where code = 'MALE'),
  'luis@sellify.pe'
),
(
  (select id from core.lookup_values where code = 'DNI'),
  '87654321',
  'María',
  'Quispe',
  (select id from core.lookup_values where code = 'FEMALE'),
  'maria@andesretail.pe'
)
on conflict (tax_id) do nothing;

------------------------------------------------------------
-- USERS
------------------------------------------------------------
insert into auth.users (identity_id, username, password_hash)
values
(
 (select id from people.identities where email = 'luis@sellify.pe'),
 'luis.admin',
 '$2a$10$C0cy85kVt8/HZhBPXf.Ng.aRDpT1haQQsOiGtFOOK8wNU.Pt66QPy' -- 123456
),
(
 (select id from people.identities where email = 'maria@andesretail.pe'),
 'maria.admin',
 '$2a$10$C0cy85kVt8/HZhBPXf.Ng.aRDpT1haQQsOiGtFOOK8wNU.Pt66QPy' -- 123456
)
on conflict (username) do nothing;

------------------------------------------------------------
-- USER COMPANIES
------------------------------------------------------------
insert into auth.user_companies (user_id, company_id, is_default)
values
(
 (select id from auth.users where username = 'luis.admin'),
 (select id from core.companies where trade_name = 'Sellify'),
 true
),
(
 (select id from auth.users where username = 'maria.admin'),
 (select id from core.companies where trade_name = 'Andes Retail'),
 true
)
on conflict do nothing;

------------------------------------------------------------
-- USER ROLES
------------------------------------------------------------
insert into auth.user_roles (user_id, role_id, is_default)
values
(
 (select id from auth.users where username = 'luis.admin'),
 (select r.id from auth.roles r
  join core.companies c ON c.id = r.company_id
  where r.name = 'Super Administrator'
  AND c.trade_name = 'Sellify'),
  true
),
(
 (select id from auth.users where username = 'maria.admin'),
 (select r.id from auth.roles r
  join core.companies c ON c.id = r.company_id
  where r.name = 'Administrator'
  AND c.trade_name = 'Andes Retail'),
  true
)
on conflict do nothing;

COMMIT;