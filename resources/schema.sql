SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS insurance_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE insurance_db;

-- ─────────────────────────────────────────────────────────────────
-- users
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id  VARCHAR(50)  NOT NULL PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    name     VARCHAR(100) NOT NULL,
    role     VARCHAR(20)  NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (user_id, password, name, role) VALUES
    ('customer1', '1234', '박수현', 'CUSTOMER'),
    ('employee1', '1234', '김직원', 'EMPLOYEE'),
    ('admin1',    '1234', '관리자', 'ADMIN')
ON DUPLICATE KEY UPDATE password=VALUES(password), name=VALUES(name), role=VALUES(role);

-- ─────────────────────────────────────────────────────────────────
-- accidents
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS accidents (
    accident_id           VARCHAR(50)  NOT NULL PRIMARY KEY,
    user_id               VARCHAR(50),
    accident_date         DATETIME,
    reported_by           VARCHAR(100),
    phone                 VARCHAR(30),
    description           VARCHAR(500),
    accident_location     VARCHAR(300),
    accident_detail       VARCHAR(1000),
    documents             VARCHAR(500),
    contract_id           VARCHAR(50),
    coverage_description  VARCHAR(200),
    coverage_limit        BIGINT DEFAULT 0,
    personal_injury_limit BIGINT DEFAULT 0,
    vehicle_info          VARCHAR(200),
    expected_repair_cost  BIGINT DEFAULT 0,
    region_code           VARCHAR(50),
    status                VARCHAR(30)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO accidents (accident_id, accident_date, reported_by, phone, description,
    accident_location, accident_detail, documents, contract_id, coverage_description,
    coverage_limit, personal_injury_limit, vehicle_info, expected_repair_cost, region_code, status)
VALUES
    ('ACC-2026-001', '2026-04-19 09:32:00', '홍길동', '010-1234-5678',
     '자동차 대물 사고', '서울 강남구 테헤란로', '신호 대기 중 후방 추돌 사고 발생',
     '사고현장사진.jpg,차량수리견적서.pdf', 'CNT-20240315-001', '자동차 대물',
     20000000, 10000000, '12가 3456 (현대 소나타)', 850000, 'SEOUL-01', 'PENDING'),
    ('ACC-2026-002', '2026-04-19 11:15:00', '김철수', '010-9876-5432',
     '차량 파손', '경기도 수원시 팔달구', '주차장 내 차량 문 충돌로 인한 파손',
     '차량파손사진.jpg,수리견적서.pdf', 'CNT-20240520-002', '자기차량손해',
     30000000, 20000000, '34나 5678 (기아 K5)', 1200000, 'GYEONGGI-01', 'PENDING'),
    ('ACC-2026-003', '2026-04-18 14:20:00', '이영희', '010-5555-1234',
     '차량 전손', '인천시 부평구 경인로', '교차로 신호 위반으로 인한 정면 충돌',
     '사고사진.jpg,전손감정서.pdf', 'CNT-20231210-003', '자기차량손해',
     50000000, 30000000, '56다 9012 (현대 그랜저)', 3500000, 'INCHEON-01', 'IN_PROGRESS')
ON DUPLICATE KEY UPDATE
    accident_date=VALUES(accident_date), reported_by=VALUES(reported_by), phone=VALUES(phone),
    description=VALUES(description), accident_location=VALUES(accident_location),
    accident_detail=VALUES(accident_detail), documents=VALUES(documents),
    contract_id=VALUES(contract_id), coverage_description=VALUES(coverage_description),
    coverage_limit=VALUES(coverage_limit), personal_injury_limit=VALUES(personal_injury_limit),
    vehicle_info=VALUES(vehicle_info), expected_repair_cost=VALUES(expected_repair_cost),
    region_code=VALUES(region_code), status=VALUES(status);

-- ─────────────────────────────────────────────────────────────────
-- claims
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS claims (
    claim_id            VARCHAR(50)  NOT NULL PRIMARY KEY,
    accident_id         VARCHAR(50),
    claimant_name       VARCHAR(100),
    claim_date          DATETIME,
    contract_id         VARCHAR(50),
    description         VARCHAR(500),
    claim_status        VARCHAR(30),
    assigned_employee   VARCHAR(50),
    settlement_amount   BIGINT DEFAULT 0,
    deductible_amount   BIGINT DEFAULT 0,
    compensation_amount BIGINT DEFAULT 0,
    bank_name           VARCHAR(100),
    account_number      VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO claims (claim_id, accident_id, claimant_name, claim_date, contract_id,
    description, claim_status, assigned_employee,
    settlement_amount, deductible_amount, compensation_amount)
VALUES
    ('CL-00001', 'ACC-2026-003', '이영희', '2026-04-18 00:00:00', 'CNT-20231210-003',
     '차량 전손', 'PAYMENT_PENDING', 'EMP-1023',
     14800000, 0, 14800000)
ON DUPLICATE KEY UPDATE
    accident_id=VALUES(accident_id), claimant_name=VALUES(claimant_name),
    claim_date=VALUES(claim_date), contract_id=VALUES(contract_id),
    description=VALUES(description), claim_status=VALUES(claim_status),
    assigned_employee=VALUES(assigned_employee), settlement_amount=VALUES(settlement_amount),
    deductible_amount=VALUES(deductible_amount),
    compensation_amount=VALUES(compensation_amount);

-- ─────────────────────────────────────────────────────────────────
-- contracts
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS contracts (
    contract_id           VARCHAR(50)  NOT NULL PRIMARY KEY,
    policy_no             VARCHAR(50),
    product_name          VARCHAR(200),
    subscription_no       VARCHAR(50),
    premium               BIGINT DEFAULT 0,
    car_number            VARCHAR(30),
    coverages_description VARCHAR(500),
    coverage_limit        VARCHAR(200),
    riders_description    VARCHAR(500),
    issue_date            DATETIME,
    start_date            DATETIME,
    end_date              DATETIME,
    status                VARCHAR(30),
    holder_name           VARCHAR(100),
    holder_party_id       VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO contracts (contract_id, policy_no, product_name, premium, car_number,
    coverages_description, coverage_limit, riders_description,
    issue_date, start_date, end_date, status, holder_name, holder_party_id)
VALUES
    ('CNT-20240315-001', 'IN-2026-001', 'MZ세대 다이렉트 개인용자동차보험', 2509200, '64마0866',
     '대인배상I, 대인배상II, 대물배상', '', '마일리지 특약',
     '2026-04-01 00:00:00', '2026-04-01 00:00:00', '2027-04-01 00:00:00',
     'ACTIVE', '박수현', 'PARTY-CNT-20240315-001'),
    ('CNT-20240520-002', 'IN-2025-002', 'MZ세대 다이렉트 개인용자동차보험', 1980000, '12가3456',
     '대인배상I, 대인배상II, 대물배상, 자기차량손해', '', '블랙박스 할인특약',
     '2025-06-15 00:00:00', '2025-06-15 00:00:00', '2026-06-15 00:00:00',
     'ACTIVE', '김직원', 'PARTY-CNT-20240520-002'),
    ('CNT-20231210-003', 'IN-2023-003', 'MZ세대 다이렉트 개인용자동차보험', 2100000, '56다9012',
     '대인배상I, 대물배상, 자기차량손해', '', '없음',
     '2023-12-10 00:00:00', '2023-12-10 00:00:00', '2024-12-10 00:00:00',
     'EXPIRED', '이영희', 'PARTY-CNT-20231210-003')
ON DUPLICATE KEY UPDATE
    policy_no=VALUES(policy_no), product_name=VALUES(product_name),
    premium=VALUES(premium), car_number=VALUES(car_number),
    coverages_description=VALUES(coverages_description), coverage_limit=VALUES(coverage_limit),
    riders_description=VALUES(riders_description), issue_date=VALUES(issue_date),
    start_date=VALUES(start_date), end_date=VALUES(end_date),
    status=VALUES(status), holder_name=VALUES(holder_name), holder_party_id=VALUES(holder_party_id);

-- ─────────────────────────────────────────────────────────────────
-- contract_selected_coverages
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS contract_selected_coverages (
    id                 VARCHAR(100) NOT NULL PRIMARY KEY,
    contract_id        VARCHAR(50)  NOT NULL,
    coverage_master_id VARCHAR(50),
    coverage_name      VARCHAR(100),
    mandatory          TINYINT(1) DEFAULT 0,
    deductible_type    VARCHAR(20),
    deductible_amount  BIGINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO contract_selected_coverages
    (id, contract_id, coverage_master_id, coverage_name, mandatory, deductible_type, deductible_amount)
VALUES
    ('CNT-20240315-001-COV-001', 'CNT-20240315-001', 'COV-001', '대인배상 I',  1, 'NONE', 0),
    ('CNT-20240315-001-COV-002', 'CNT-20240315-001', 'COV-002', '대인배상 II', 0, 'NONE', 0),
    ('CNT-20240315-001-COV-003', 'CNT-20240315-001', 'COV-003', '대물배상',    0, 'NONE', 0),
    ('CNT-20240520-002-COV-001', 'CNT-20240520-002', 'COV-001', '대인배상 I',  1, 'NONE', 0),
    ('CNT-20240520-002-COV-002', 'CNT-20240520-002', 'COV-002', '대인배상 II', 0, 'NONE', 0),
    ('CNT-20240520-002-COV-003', 'CNT-20240520-002', 'COV-003', '대물배상',    0, 'NONE', 0),
    ('CNT-20240520-002-COV-005', 'CNT-20240520-002', 'COV-005', '자기차량손해',0, 'FIXED', 200000),
    ('CNT-20231210-003-COV-001', 'CNT-20231210-003', 'COV-001', '대인배상 I',  1, 'NONE', 0),
    ('CNT-20231210-003-COV-003', 'CNT-20231210-003', 'COV-003', '대물배상',    0, 'NONE', 0),
    ('CNT-20231210-003-COV-005', 'CNT-20231210-003', 'COV-005', '자기차량손해',0, 'FIXED', 200000)
ON DUPLICATE KEY UPDATE
    coverage_name=VALUES(coverage_name), mandatory=VALUES(mandatory),
    deductible_type=VALUES(deductible_type), deductible_amount=VALUES(deductible_amount);

-- ─────────────────────────────────────────────────────────────────
-- subscriptions
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS subscriptions (
    subscription_no       VARCHAR(50)  NOT NULL PRIMARY KEY,
    user_id               VARCHAR(50),
    applicant_name        VARCHAR(100),
    ssn                   VARCHAR(20),
    address               VARCHAR(300),
    car_number            VARCHAR(30),
    chassis_number        VARCHAR(50),
    product_name          VARCHAR(200),
    premium               BIGINT DEFAULT 0,
    base_premium          BIGINT DEFAULT 0,
    subscription_date     DATETIME,
    status                VARCHAR(30),
    occupation            VARCHAR(100),
    age                   INT DEFAULT 0,
    coverages_description VARCHAR(500),
    reject_reason         VARCHAR(500),
    supplement_documents  VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO subscriptions (subscription_no, applicant_name, ssn, address, car_number,
    product_name, premium, base_premium, subscription_date, status, age, coverages_description)
VALUES
    ('20260418-0001', '홍길동', '020101-3123456', '서울시 강남구', '64마0866',
     'MZ세대 다이렉트 개인용자동차보험', 2509200, 2200000, '2026-04-18 00:00:00',
     'PENDING_REVIEW', 24, '대인배상I,대인배상II,대물배상')
ON DUPLICATE KEY UPDATE
    applicant_name=VALUES(applicant_name), ssn=VALUES(ssn), address=VALUES(address),
    car_number=VALUES(car_number), product_name=VALUES(product_name),
    premium=VALUES(premium), base_premium=VALUES(base_premium),
    subscription_date=VALUES(subscription_date), status=VALUES(status),
    age=VALUES(age), coverages_description=VALUES(coverages_description);

-- ─────────────────────────────────────────────────────────────────
-- coverages (master)
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS coverages (
    coverage_id   VARCHAR(50)  NOT NULL PRIMARY KEY,
    coverage_name VARCHAR(100) NOT NULL,
    coverage_type VARCHAR(50),
    mandatory     TINYINT(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO coverages (coverage_id, coverage_name, coverage_type, mandatory) VALUES
    ('COV-001', '대인배상 I',   'PERSONAL_INJURY_MANDATORY', 1),
    ('COV-002', '대인배상 II',  'PERSONAL_INJURY_OPTIONAL',  0),
    ('COV-003', '대물배상',     'PROPERTY_DAMAGE',           0),
    ('COV-004', '자동차상해',   'AUTO_INJURY',               0),
    ('COV-005', '자기차량손해', 'OWN_VEHICLE_DAMAGE',        0),
    ('COV-006', '무보험차상해', 'UNINSURED_VEHICLE',         0)
ON DUPLICATE KEY UPDATE coverage_name=VALUES(coverage_name), coverage_type=VALUES(coverage_type), mandatory=VALUES(mandatory);

-- ─────────────────────────────────────────────────────────────────
-- coverage_limit_options
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS coverage_limit_options (
    option_id          VARCHAR(50) NOT NULL PRIMARY KEY,
    coverage_master_id VARCHAR(50) NOT NULL,
    seq                INT DEFAULT 1,
    option_name        VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO coverage_limit_options (option_id, coverage_master_id, seq, option_name) VALUES
    ('OPT-001-1', 'COV-001', 1, '기본 옵션'),
    ('OPT-002-1', 'COV-002', 1, '한도5억'),
    ('OPT-002-2', 'COV-002', 2, '무한'),
    ('OPT-003-1', 'COV-003', 1, '기본옵션'),
    ('OPT-004-1', 'COV-004', 1, '기본옵션'),
    ('OPT-005-1', 'COV-005', 1, '기본옵션'),
    ('OPT-006-1', 'COV-006', 1, '기본옵션')
ON DUPLICATE KEY UPDATE option_name=VALUES(option_name), seq=VALUES(seq);

-- ─────────────────────────────────────────────────────────────────
-- employees (field investigators)
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS employees (
    employee_id     VARCHAR(50)  NOT NULL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    specialty       VARCHAR(100),
    open_case_count INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO employees (employee_id, name, specialty, open_case_count) VALUES
    ('EMP-1023', '김현수', '자동차 대물',  3),
    ('EMP-1024', '이수진', '자동차 대인',  1),
    ('EMP-1025', '박민준', '자기차량손해', 5),
    ('EMP-1026', '최영희', '자동차 대물',  2)
ON DUPLICATE KEY UPDATE name=VALUES(name), specialty=VALUES(specialty), open_case_count=VALUES(open_case_count);

-- ─────────────────────────────────────────────────────────────────
-- riders
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS riders (
    rider_id      VARCHAR(50)  NOT NULL PRIMARY KEY,
    rider_code    VARCHAR(50)  NOT NULL,
    rider_name    VARCHAR(100) NOT NULL,
    description   TEXT,
    rider_type    VARCHAR(30),
    mandatory     TINYINT(1) DEFAULT 0,
    discount_rate DOUBLE DEFAULT 0.0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO riders (rider_id, rider_code, rider_name, description, rider_type, mandatory, discount_rate) VALUES
    ('R-001', 'RC-MILEAGE',  '마일리지 특약',
     '연간 주행거리 1만km 이하 시 보험료 10% 할인', 'MILEAGE', 0, 0.10),
    ('R-002', 'RC-SAFETY',   '안전장치 할인특약',
     '에어백·ABS 장착 차량 보험료 5% 할인', 'SAFETY', 0, 0.05),
    ('R-003', 'RC-BLACKBOX', '블랙박스 할인특약',
     '블랙박스 장착 차량 보험료 3% 할인', 'DISCOUNT', 0, 0.03)
ON DUPLICATE KEY UPDATE
    rider_code=VALUES(rider_code), rider_name=VALUES(rider_name), description=VALUES(description),
    rider_type=VALUES(rider_type), mandatory=VALUES(mandatory), discount_rate=VALUES(discount_rate);

-- ─────────────────────────────────────────────────────────────────
-- products
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS products (
    product_id       VARCHAR(50)  NOT NULL PRIMARY KEY,
    product_code     VARCHAR(50)  NOT NULL,
    product_name     VARCHAR(200) NOT NULL,
    description      TEXT,
    line_of_business VARCHAR(30),
    sale_start_date  DATETIME,
    sale_end_date    DATETIME,
    status           VARCHAR(30),
    target           VARCHAR(30),
    created_at       DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO products (product_id, product_code, product_name, description, line_of_business,
    sale_start_date, sale_end_date, status, target, created_at)
VALUES
    ('PROD-001', 'AUTO-MZ-2024', 'MZ세대 다이렉트 개인용자동차보험',
     'MZ 세대를 위한 다이렉트 자동차보험입니다.',
     'AUTO', '2024-01-01 00:00:00', '2027-12-31 00:00:00', 'ON_SALE', 'PERSONAL', NOW()),
    ('PROD-002', 'AUTO-BIZ-2025', '업무용 자동차보험',
     '법인 및 사업자 업무용 차량을 위한 자동차보험입니다.',
     'AUTO', '2025-03-01 00:00:00', '2028-02-28 00:00:00', 'ON_SALE', 'BUSINESS', NOW()),
    ('PROD-003', 'AUTO-COM-2025', '영업용 자동차보험',
     '택시·화물 등 영업용 차량을 위한 자동차보험입니다.',
     'AUTO', '2025-06-01 00:00:00', '2028-05-31 00:00:00', 'ON_SALE', 'COMMERCIAL', NOW()),
    ('PROD-004', 'AUTO-MZ-2025', 'MZ세대 다이렉트 개인용자동차보험 2025',
     '2025년 개정 약관을 적용한 다이렉트 자동차보험입니다.',
     'AUTO', '2025-09-01 00:00:00', '2028-08-31 00:00:00', 'SALE_PENDING', 'PERSONAL', NOW()),
    ('PROD-005', 'AUTO-YOUNG-2025', '젊은운전자 특화보험',
     '만 18~25세 초보운전자를 위한 특화 자동차보험입니다.',
     'AUTO', '2025-10-01 00:00:00', '2028-09-30 00:00:00', 'SALE_PENDING', 'PERSONAL', NOW())
ON DUPLICATE KEY UPDATE
    product_code=VALUES(product_code), product_name=VALUES(product_name),
    description=VALUES(description), line_of_business=VALUES(line_of_business),
    sale_start_date=VALUES(sale_start_date), sale_end_date=VALUES(sale_end_date),
    status=VALUES(status), target=VALUES(target);

-- ─────────────────────────────────────────────────────────────────
-- product_coverages
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS product_coverages (
    product_coverage_id VARCHAR(100) NOT NULL PRIMARY KEY,
    product_id          VARCHAR(50)  NOT NULL,
    coverage_master_id  VARCHAR(50),
    coverage_name       VARCHAR(100),
    coverage_type       VARCHAR(50),
    mandatory           TINYINT(1) DEFAULT 0,
    limit_amount        BIGINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO product_coverages (product_coverage_id, product_id, coverage_master_id, coverage_name, mandatory) VALUES
    ('PC-PROD-001-COV-001', 'PROD-001', 'COV-001', '대인배상 I',   1),
    ('PC-PROD-001-COV-002', 'PROD-001', 'COV-002', '대인배상 II',  0),
    ('PC-PROD-001-COV-003', 'PROD-001', 'COV-003', '대물배상',     0),
    ('PC-PROD-001-COV-004', 'PROD-001', 'COV-004', '자동차상해',   0),
    ('PC-PROD-001-COV-005', 'PROD-001', 'COV-005', '자기차량손해', 0),
    ('PC-PROD-001-COV-006', 'PROD-001', 'COV-006', '무보험차상해', 0)
ON DUPLICATE KEY UPDATE coverage_name=VALUES(coverage_name), mandatory=VALUES(mandatory);

-- ─────────────────────────────────────────────────────────────────
-- product_riders
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS product_riders (
    product_rider_id VARCHAR(100) NOT NULL PRIMARY KEY,
    product_id       VARCHAR(50)  NOT NULL,
    rider_id         VARCHAR(50),
    rider_code       VARCHAR(50),
    rider_name       VARCHAR(100),
    discount_rate    DOUBLE DEFAULT 0.0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO product_riders (product_rider_id, product_id, rider_id, rider_code, rider_name, discount_rate) VALUES
    ('PR-PROD-001-R-001', 'PROD-001', 'R-001', 'RC-MILEAGE',  '마일리지 특약',     0.10),
    ('PR-PROD-001-R-002', 'PROD-001', 'R-002', 'RC-SAFETY',   '안전장치 할인특약', 0.05),
    ('PR-PROD-001-R-003', 'PROD-001', 'R-003', 'RC-BLACKBOX', '블랙박스 할인특약', 0.03)
ON DUPLICATE KEY UPDATE rider_name=VALUES(rider_name), discount_rate=VALUES(discount_rate);

-- ─────────────────────────────────────────────────────────────────
-- product_documents
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS product_documents (
    product_document_id VARCHAR(100) NOT NULL PRIMARY KEY,
    product_id          VARCHAR(50)  NOT NULL,
    doc_type            VARCHAR(50),
    title               VARCHAR(200),
    note                TEXT,
    filename            VARCHAR(255),
    file_path           VARCHAR(500),
    created_at          DATETIME,
    submitted_at        DATETIME,
    received_at         DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO product_documents (product_document_id, product_id, doc_type, title, note, created_at) VALUES
    ('DOC-001', 'PROD-001', 'GENERAL_TERMS', '보통약관',
     '■ 보통약관\n\n제1조 (보험계약의 성립)\n  보험계약은 계약자가 청약하고 보험자가 승낙함으로써 성립합니다.\n',
     NOW()),
    ('DOC-002', 'PROD-001', 'SPECIAL_TERMS', '특별약관',
     '■ 특별약관\n\n제1조 (마일리지 특약)\n  연간 주행거리에 따라 보험료를 환급합니다.\n',
     NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title), note=VALUES(note);

-- ─────────────────────────────────────────────────────────────────
-- damage_investigations
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS damage_investigations (
    investigation_id     VARCHAR(50)  NOT NULL PRIMARY KEY,
    accident_id          VARCHAR(50)  NOT NULL,
    claim_id             VARCHAR(50),
    investigator_name    VARCHAR(100),
    investigation_date   DATETIME,
    opinion              VARCHAR(1000),
    damage_code          VARCHAR(50),
    injury_grade         INT DEFAULT 0,
    our_fault            INT DEFAULT 0,
    other_fault          INT DEFAULT 0,
    liability            VARCHAR(200),
    expected_repair_cost BIGINT DEFAULT 0,
    compensation_limit   BIGINT DEFAULT 0,
    final_opinion        VARCHAR(1000),
    saved_at             DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─────────────────────────────────────────────────────────────────
-- base_rates (기초율 요율 계수)
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS base_rates (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    rate_type      VARCHAR(50)  NOT NULL,
    dimension1     VARCHAR(100) NOT NULL,
    dimension2     VARCHAR(100),
    rate_value     DOUBLE       NOT NULL,
    effective_year INT          NOT NULL,
    note           VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO base_rates (rate_type, dimension1, dimension2, rate_value, effective_year, note) VALUES
    ('VEHICLE_TYPE', '소형 A',      NULL, 0.4718, 2025, '소형A (경차·소형 세단)'),
    ('VEHICLE_TYPE', '소형 B',      NULL, 1.4730, 2025, '소형B (준중형 세단)'),
    ('VEHICLE_TYPE', '중형',        NULL, 2.0074, 2025, '중형 세단'),
    ('VEHICLE_TYPE', '대형',        NULL, 1.4252, 2025, '대형 세단·SUV'),
    ('VEHICLE_TYPE', '다인승승용2종',NULL, 0.6225, 2025, '미니밴·11인승 이하'),
    ('DRIVER_AGE',   '21세미만',    NULL, 3.2000, 2025, '21세 미만 초보운전자 할증'),
    ('DRIVER_AGE',   '21~25세',     NULL, 1.8500, 2025, '21~25세'),
    ('DRIVER_AGE',   '26~30세',     NULL, 1.3000, 2025, '26~30세'),
    ('DRIVER_AGE',   '31~40세',     NULL, 1.0000, 2025, '기준연령대 (기본요율 1.0)'),
    ('DRIVER_AGE',   '41~50세',     NULL, 0.9200, 2025, '41~50세'),
    ('DRIVER_AGE',   '51~60세',     NULL, 0.9800, 2025, '51~60세'),
    ('DRIVER_AGE',   '61세이상',    NULL, 1.2500, 2025, '61세 이상'),
    ('COVERAGE',     '대인배상(I)',  '개인용', 0.1185, 2025, '의무담보 기준 순보험료율'),
    ('COVERAGE',     '대인배상(II)', '개인용', 0.2227, 2025, '임의담보'),
    ('COVERAGE',     '대물배상(임의)','개인용',0.3854, 2025, '임의담보'),
    ('COVERAGE',     '자기차량손해', '개인용', 0.2663, 2025, '자차 손해율'),
    ('COVERAGE',     '자손자기신체', '개인용', 0.0304, 2025, '자손·자상 손해율'),
    ('COVERAGE',     '무보험상해',   '개인용', 0.0022, 2025, '무보험차 상해'),
    ('DRIVER_LIMIT', '기명1인',      NULL, 0.7800, 2025, '기명피보험자 1인 한정 할인'),
    ('DRIVER_LIMIT', '부부운전',     NULL, 0.8500, 2025, '부부운전 한정 할인'),
    ('DRIVER_LIMIT', '가족한정',     NULL, 0.9200, 2025, '가족 한정 할인'),
    ('DRIVER_LIMIT', '미가입',       NULL, 1.0000, 2025, '한정 없음 (기본)');

-- ─────────────────────────────────────────────────────────────────
-- base_rate_stats (기초율 통계 원본 - CSV 임포트)
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS base_rate_stats (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    stat_type          VARCHAR(50)  NOT NULL,
    stat_year          INT          NOT NULL,
    dimension1         VARCHAR(100),
    dimension2         VARCHAR(100),
    dimension3         VARCHAR(100),
    loss_amount        BIGINT DEFAULT 0,
    death_count        INT    DEFAULT 0,
    injury_count       INT    DEFAULT 0,
    total_loss_count   INT    DEFAULT 0,
    partial_loss_count INT    DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO base_rate_stats (stat_type,stat_year,dimension1,dimension2,dimension3,loss_amount,death_count,injury_count,total_loss_count,partial_loss_count) VALUES
    ('VEHICLE_TYPE',2025,'1.소형 A',NULL,NULL,803871764,208,250551,10467,269721),
    ('VEHICLE_TYPE',2025,'2.소형 B',NULL,NULL,2509936850,492,647579,22039,880439),
    ('VEHICLE_TYPE',2025,'3.중형',NULL,NULL,3420415006,604,791916,29029,1045197),
    ('VEHICLE_TYPE',2025,'4.대형',NULL,NULL,2428390846,387,512401,18284,767349),
    ('VEHICLE_TYPE',2025,'5.다인승승용1종',NULL,NULL,65842,0,23,0,34),
    ('VEHICLE_TYPE',2025,'6.다인승승용2종',NULL,NULL,1060723792,225,249063,7705,349318),
    ('VEHICLE_TYPE',2024,'1.소형 A',NULL,NULL,756000000,190,235000,9800,252000),
    ('VEHICLE_TYPE',2024,'2.소형 B',NULL,NULL,2380000000,460,610000,20500,830000),
    ('VEHICLE_TYPE',2024,'3.중형',NULL,NULL,3200000000,575,750000,27000,990000),
    ('VEHICLE_TYPE',2024,'4.대형',NULL,NULL,2290000000,362,485000,17100,725000),
    ('VEHICLE_TYPE',2024,'5.다인승승용1종',NULL,NULL,61000,0,20,0,30),
    ('VEHICLE_TYPE',2024,'6.다인승승용2종',NULL,NULL,1010000000,210,235000,7200,330000),
    ('GENDER_AGE',2025,'1.남성','01.20세이하',NULL,-2135601,0,-95,-1,-12),
    ('GENDER_AGE',2025,'1.남성','02.21세~25세',NULL,12932947,1,2991,116,3378),
    ('GENDER_AGE',2025,'1.남성','03.26세~30세',NULL,315825601,55,78434,2652,95250),
    ('GENDER_AGE',2025,'1.남성','04.31세~35세',NULL,572820706,85,137670,4569,176531),
    ('GENDER_AGE',2025,'1.남성','05.36세~40세',NULL,629158227,98,148666,5326,202443),
    ('GENDER_AGE',2025,'1.남성','06.41세~45세',NULL,789241957,124,184740,6928,254929),
    ('GENDER_AGE',2025,'1.남성','07.46세~50세',NULL,759716298,131,180276,7001,247275),
    ('GENDER_AGE',2025,'1.남성','08.51세~55세',NULL,929957259,182,224010,8328,303745),
    ('GENDER_AGE',2025,'1.남성','09.56세~60세',NULL,970080178,180,233899,8648,314905),
    ('GENDER_AGE',2025,'1.남성','10.61세이상',NULL,2040944041,482,479228,17487,659080),
    ('GENDER_AGE',2025,'2.여성','02.21세~25세',NULL,8762287,0,1650,62,1968),
    ('GENDER_AGE',2025,'2.여성','03.26세~30세',NULL,151838420,28,40083,1167,52328),
    ('GENDER_AGE',2025,'2.여성','04.31세~35세',NULL,252848791,26,63255,1927,84986),
    ('GENDER_AGE',2025,'2.여성','05.36세~40세',NULL,272746808,22,66446,2291,90954),
    ('GENDER_AGE',2025,'2.여성','06.41세~45세',NULL,366519795,48,88628,2991,121165),
    ('GENDER_AGE',2025,'2.여성','07.46세~50세',NULL,389897659,72,94661,3340,129786),
    ('GENDER_AGE',2025,'2.여성','08.51세~55세',NULL,497634344,98,123556,4268,164190),
    ('GENDER_AGE',2025,'2.여성','09.56세~60세',NULL,475624032,108,117892,3938,156292),
    ('GENDER_AGE',2025,'2.여성','10.61세이상',NULL,788980104,176,185541,6486,252864),
    ('COVERAGE',2025,'개인용자동차보험','대물배상(임의)',NULL,3941048878,0,0,25939,2074062),
    ('COVERAGE',2025,'개인용자동차보험','대인배상(I)',NULL,1211706274,1082,1255634,0,0),
    ('COVERAGE',2025,'개인용자동차보험','대인배상(II)',NULL,2277172029,459,1065514,0,0),
    ('COVERAGE',2025,'개인용자동차보험','무보험상해(대인)',NULL,22661011,56,10729,63,1976),
    ('COVERAGE',2025,'개인용자동차보험','자기차량손해',NULL,2722898419,0,0,63627,1314270),
    ('COVERAGE',2025,'개인용자동차보험','자손자기신체사고',NULL,310624966,364,186191,0,0),
    ('COVERAGE',2025,'업무용자동차보험','대물배상(임의)',NULL,1360994811,0,0,9412,622799),
    ('COVERAGE',2025,'업무용자동차보험','대인배상(I)',NULL,341757251,583,307703,0,0),
    ('COVERAGE',2025,'업무용자동차보험','대인배상(II)',NULL,635061767,231,260736,0,0),
    ('COVERAGE',2025,'업무용자동차보험','자기차량손해',NULL,615742675,0,0,10591,258222),
    ('COVERAGE',2025,'영업용자동차보험','대물배상(임의)',NULL,580070918,0,0,4948,226071),
    ('COVERAGE',2025,'영업용자동차보험','대인배상(I)',NULL,152170007,268,130234,0,0),
    ('COVERAGE',2025,'영업용자동차보험','대인배상(II)',NULL,308461650,102,115719,0,0),
    ('COVERAGE',2025,'영업용자동차보험','자기차량손해',NULL,46005517,0,0,443,19450),
    ('DRIVER_AGE_LIMIT',2025,'미가입','전연령',NULL,19584065,6,2839,513,2340),
    ('DRIVER_AGE_LIMIT',2025,'미가입','21세이상',NULL,15318026,4,3769,166,4099),
    ('DRIVER_AGE_LIMIT',2025,'미가입','24세이상',NULL,19816117,0,5130,221,5643),
    ('DRIVER_AGE_LIMIT',2025,'미가입','26세이상',NULL,70421479,14,17023,573,21304),
    ('DRIVER_AGE_LIMIT',2025,'미가입','30세이상',NULL,129913829,7,30111,992,39382),
    ('DRIVER_AGE_LIMIT',2025,'미가입','35세이상',NULL,164358425,33,36274,1307,48756),
    ('DRIVER_AGE_LIMIT',2025,'미가입','43세이상',NULL,78145333,19,16924,637,22943),
    ('DRIVER_AGE_LIMIT',2025,'미가입','48세이상',NULL,98178355,20,22196,718,29253),
    ('DRIVER_AGE_LIMIT',2025,'가족','전연령',NULL,34155469,6,8649,292,11022),
    ('DRIVER_AGE_LIMIT',2025,'가족','21세이상',NULL,58329563,13,15386,609,17669),
    ('DRIVER_AGE_LIMIT',2025,'가족','26세이상',NULL,222641530,39,57196,2109,72705),
    ('DRIVER_AGE_LIMIT',2025,'가족','30세이상',NULL,306247980,54,77409,2644,100576),
    ('DRIVER_AGE_LIMIT',2025,'가족','35세이상',NULL,361041340,78,87011,3122,119326),
    ('DRIVER_AGE_LIMIT',2025,'부부운전','30세이상',NULL,326360182,49,78463,2307,112495),
    ('DRIVER_AGE_LIMIT',2025,'부부운전','35세이상',NULL,744062136,81,172396,5793,252093),
    ('DRIVER_AGE_LIMIT',2025,'부부운전','43세이상',NULL,588755404,66,138156,4803,201793),
    ('DRIVER_AGE_LIMIT',2025,'부부운전','48세이상',NULL,1828055158,323,417393,15351,617644),
    ('DRIVER_AGE_LIMIT',2025,'기명피보험자1인','30세이상',NULL,453349295,63,115590,3802,140974),
    ('DRIVER_AGE_LIMIT',2025,'기명피보험자1인','35세이상',NULL,514429611,81,124455,4783,157085),
    ('DRIVER_AGE_LIMIT',2025,'기명피보험자1인','43세이상',NULL,323141515,59,79794,3107,102125),
    ('DRIVER_AGE_LIMIT',2025,'기명피보험자1인','48세이상',NULL,1788363456,473,442330,15541,576850);

-- ─────────────────────────────────────────────────────────────────
-- exclusions (면책사유)
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS exclusions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    exclusion_type  VARCHAR(50)  NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS exclusion_sub_items (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    exclusion_id INT          NOT NULL,
    content      TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO exclusions (exclusion_type, name, description) VALUES
    ('WILLFUL','고의사고','피보험자의 고의로 인한 손해는 보상하지 않습니다.'),
    ('WILLFUL','자살·자해','피보험자의 자살 또는 자해로 인한 손해는 보상하지 않습니다.'),
    ('WAR','전쟁·내란','전쟁, 혁명, 내란, 사변, 폭동, 소요로 인한 손해는 보상하지 않습니다.'),
    ('WAR','테러·핵','핵연료물질·방사성 오염, 테러로 인한 손해는 보상하지 않습니다.'),
    ('ILLEGAL','무면허운전','무면허 상태로 운전 중 발생한 사고는 보상하지 않습니다.'),
    ('ILLEGAL','음주운전','혈중알콜농도 0.03% 이상인 상태로 운전 중 발생한 사고는 보상하지 않습니다.'),
    ('ILLEGAL','마약·약물','마약, 각성제, 흡입제 등 약물 복용 상태에서 운전 중 발생한 사고는 보상하지 않습니다.'),
    ('RACING','경주·시험','자동차 경주, 속도경쟁, 곡예운전 중 발생한 손해는 보상하지 않습니다.'),
    ('MISUSE','용도위반','보험증권에 기재된 용도와 다르게 사용 중 발생한 손해는 보상하지 않습니다.'),
    ('SCOPE','운전자한정위반','운전자 한정 특약에서 정한 운전자 외의 사람이 운전 중 발생한 손해는 보상하지 않습니다.'),
    ('SCOPE','연령한정위반','운전자 연령 한정 특약에서 정한 연령 미만의 운전자가 운전 중 발생한 손해는 보상하지 않습니다.'),
    ('MAINTENANCE','차량결함','법령에 의한 자동차 검사를 받지 아니하였거나 안전 기준에 현저히 미달하는 차량 운행 중 발생한 손해는 보상하지 않습니다.'),
    ('OTHER','지진·화산','지진, 화산 폭발로 인한 손해는 보상하지 않습니다.'),
    ('OTHER','영업손실','보험사고로 인한 영업손실, 시세하락, 간접손해는 보상하지 않습니다.');

INSERT INTO exclusion_sub_items (exclusion_id, content) VALUES
    (1, '보험계약자가 고의로 사고를 일으킨 경우'),
    (1, '피보험자가 고의로 자신이 상해를 입은 경우'),
    (5, '도로교통법 또는 건설기계관리법에 의한 운전면허를 취득하지 아니한 자가 운전한 경우'),
    (5, '운전면허의 효력이 정지된 상태에서 운전한 경우'),
    (6, '도로교통법 제44조에 의한 음주 측정에 불응한 경우 포함'),
    (10, '기명피보험자, 기명피보험자의 배우자 이외의 자가 운전한 경우 (부부 한정 위반)'),
    (11, '보험증권에 기재된 연령 미만의 운전자가 사고를 야기한 경우');

-- ─────────────────────────────────────────────────────────────────
-- standard_provisions (표준약관)
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS standard_provisions (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    provision_type VARCHAR(50)  NOT NULL,
    title          VARCHAR(200) NOT NULL,
    description    TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS provision_items (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    provision_id   INT          NOT NULL,
    article_no     INT          NOT NULL,
    article_title  VARCHAR(200),
    content        TEXT,
    parent_id      INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO standard_provisions (provision_type, title, description) VALUES
    ('GENERAL','개인용자동차보험 보통약관','금융감독원 표준약관 기준. 개인 소유 비사업용 자동차에 적용됩니다.'),
    ('GENERAL','업무용자동차보험 보통약관','법인 및 사업자가 업무 목적으로 사용하는 자동차에 적용됩니다.'),
    ('SPECIAL','대인배상 특별약관','대인배상 I·II 담보에 관한 세부 지급 기준 및 면책 조항.'),
    ('SPECIAL','자기차량손해 특별약관','자기차량손해 담보의 보상 범위, 자기부담금, 면책 사유를 규정합니다.'),
    ('SPECIAL','마일리지 특약','연간 주행거리에 따른 보험료 환급 조건 및 정산 방법을 규정합니다.');

INSERT INTO provision_items (provision_id, article_no, article_title, content, parent_id) VALUES
    (1, 1, '보험계약의 성립',
     '보험계약은 계약자가 청약하고 보험회사가 승낙함으로써 성립합니다. 보험회사는 청약을 받은 날부터 30일 이내에 승낙 여부를 계약자에게 통지합니다.', NULL),
    (1, 2, '보험기간',
     '보험기간은 보험증권에 기재된 보험기간의 첫날 00시부터 마지막 날 24시까지로 합니다.', NULL),
    (1, 3, '보험료의 납입',
     '계약자는 제1회 보험료를 계약 성립 후 지체 없이 납입하여야 합니다. 제1회 보험료 납입 전에 사고가 발생한 경우에는 보상하지 않습니다.', NULL),
    (1, 4, '보험료 환급',
     '보험계약이 해지된 경우 경과하지 않은 기간의 보험료를 환급합니다. 단, 계약자의 고의나 중과실로 계약이 취소된 경우에는 환급하지 않습니다.', NULL),
    (1, 5, '손해방지의무',
     '사고 발생 시 계약자 또는 피보험자는 즉시 손해 방지 및 경감을 위한 조치를 취하여야 합니다. 이를 이행하지 않아 손해가 확대된 경우 확대된 부분은 보상하지 않습니다.', NULL),
    (1, 6, '사고 통지',
     '사고 발생 시 계약자 또는 피보험자는 지체 없이 보험회사에 통지하여야 합니다. 통지 의무를 위반하여 손해가 확대된 경우 확대된 부분은 보상하지 않습니다.', NULL),
    (1, 7, '보험금 청구',
     '보험금은 사고 발생일로부터 3년 이내에 청구하여야 합니다. 보험금을 청구하고자 하는 경우 사고 발생 경위서, 피해 확인 서류 등을 제출하여야 합니다.', NULL),
    (1, 8, '보험금 지급',
     '보험회사는 보험금 청구 서류를 접수한 날부터 3영업일 이내에 보험금을 지급합니다. 단, 손해 사정이 완료되지 않은 경우 그 기간을 연장할 수 있습니다.', NULL),
    (1, 9, '보험사기 방지',
     '계약자, 피보험자 또는 이들의 대리인이 고의로 사고를 유발하거나 보험금 청구 서류를 위·변조한 경우 보험금을 지급하지 않으며 계약을 해지할 수 있습니다.', NULL),
    (1, 10, '관할법원',
     '이 계약에 관한 소송은 계약자 또는 피보험자의 주소지 법원 또는 보험회사의 본사 소재지 법원을 관할로 합니다.', NULL),
    (3, 1, '대인배상 I 보상 범위',
     '「자동차손해배상 보장법」에 의한 의무보험으로, 피보험자가 피보험자동차를 소유·사용·관리하는 동안 생긴 사고로 타인을 사망하게 하거나 부상시킨 경우 보상합니다.', NULL),
    (3, 2, '대인배상 II 보상 범위',
     '대인배상 I의 한도를 초과하는 손해를 보상합니다. 무한 또는 계약시 정한 한도 내에서 보상합니다.', NULL),
    (3, 3, '지급 기준',
     '사망: 상실수익액 + 장례비 / 부상: 치료비 + 휴업손해 + 위자료 / 후유장해: 상실수익액 + 위자료', NULL),
    (4, 1, '자기차량손해 보상 범위',
     '피보험자동차가 충돌, 접촉, 추락, 전복, 화재, 폭발, 도난, 자연재해 등으로 인해 발생한 직접 손해를 보상합니다.', NULL),
    (4, 2, '자기부담금',
     '사고 1건당 자기부담금은 보험증권에 기재된 금액으로 하며, 통상 20만원 또는 수리비의 20% 중 큰 금액입니다.', NULL),
    (5, 1, '마일리지 적용 기준',
     '보험기간 1년 동안의 주행거리를 연간환산하여 1만km 이하인 경우 보험료의 일정 비율을 환급합니다.', NULL),
    (5, 2, '마일리지 확인 방법',
     '계약자는 보험 만기 시 차량 계기판 사진 또는 차량 검사 기록을 제출하여 주행거리를 확인받습니다.', NULL);

-- ─────────────────────────────────────────────────────────────────
-- risk_analysis_reports
-- ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS risk_analysis_reports (
    subscription_no         VARCHAR(50) NOT NULL PRIMARY KEY,
    risk_score              DOUBLE DEFAULT 0.0,
    risk_grade              INT DEFAULT 0,
    accident_score          DOUBLE DEFAULT 0.0,
    driving_exp_score       DOUBLE DEFAULT 0.0,
    credit_grade_score      DOUBLE DEFAULT 0.0,
    traffic_violation_score DOUBLE DEFAULT 0.0,
    surcharge_rate          DOUBLE DEFAULT 0.0,
    base_premium            BIGINT DEFAULT 0,
    surcharge_amount        BIGINT DEFAULT 0,
    total_premium           BIGINT DEFAULT 0,
    review_guide            VARCHAR(500),
    reviewer_name           VARCHAR(100),
    review_date             DATETIME,
    review_opinion          VARCHAR(1000)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
