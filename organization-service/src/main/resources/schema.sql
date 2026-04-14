-- ORGANIZATION TABLE
CREATE TABLE IF NOT EXISTS organization (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255)
);

-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    organization_id BIGINT,
    CONSTRAINT fk_org
    FOREIGN KEY (organization_id)
    REFERENCES organization(id)
    ON DELETE CASCADE
 );

-- PROJECT TABLE
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    organization_id BIGINT,
    CONSTRAINT fk_proj_org
    FOREIGN KEY (organization_id)
    REFERENCES organization(id)
    ON DELETE CASCADE
);