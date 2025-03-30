CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
    birthday VARCHAR(10),
    email VARCHAR(100),
    nickname VARCHAR(50),
    password VARCHAR(100),
    phone_number VARCHAR(13),
    user_role ENUM('ROLE_ADMIN', 'ROLE_DIRECTOR', 'ROLE_USER'),
    created_at DATETIME(6),
    modified_at DATETIME(6)
)

CREATE TABLE shows (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    directer_id BIGINT NOT NULL,
    title VARCHAR(255),
    content VARCHAR(255),
    category ENUM('CLASSIC', 'CONCERT', 'MUSICAL'),
    region ENUM('BUSAN', 'DAEGU', 'DAEJEON', 'GWANGJU', 'GYEONGGI', 'INCHEON', 'SEOUL', 'ULSAN'),
    status ENUM('DELETED', 'EXPIRED', 'NOT_DELETED'),
    total_seats INTEGER NOT NULL,
    start_date DATETIME(6),
    end_date DATETIME(6),
    reservation_start_date DATETIME(6),
    reservation_end_date DATETIME(6),
    created_at DATETIME(6),
    modified_at DATETIME(6)
)

CREATE TABLE seats (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    show_id BIGINT,
    name ENUM('A','R','S','STANDING','SVIP','VIP'),
    count INTEGER NOT NULL,
    price INTEGER NOT NULL,
    remain_seat_count INTEGER NOT NULL
)

CREATE TABLE tickets (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    status ENUM('CANCELED', 'PURCHASED'),
    created_at DATETIME(6),
    modified_at DATETIME(6)
)