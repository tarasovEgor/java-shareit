--DROP SEQUENCE IF EXISTS user_sequence;
--DROP SEQUENCE IF EXISTS item_sequence;
--DROP SEQUENCE IF EXISTS request_sequence;
--DROP SEQUENCE IF EXISTS comment_sequence;
--DROP SEQUENCE IF EXISTS booking_sequence;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

--<!-- Liquibase migration definition -->
--<!-- as described above -->
--CREATE TYPE my_enum_type AS ENUM('WAITING', 'APPROVED', 'REJECTED', 'CURRENT', 'PAST', 'FUTURE', 'ALL');
--CREATE CAST (my_enum_type AS my_enum_type) force;
--<!-- add an additional type -->
--<sql>CREATE CAST (varchar AS booking_status_type) WITH INOUT AS IMPLICIT</sql>


--CREATE SEQUENCE IF NOT EXISTS user_sequence
--    START WITH 1
--    INCREMENT BY 1
--    CACHE 25;
--
--CREATE SEQUENCE IF NOT EXISTS item_sequence
--    START WITH 1
--    INCREMENT BY 1
--    CACHE 25;
--
--CREATE SEQUENCE IF NOT EXISTS request_sequence
--    START WITH 1
--    INCREMENT BY 1
--    CACHE 30;
--
--CREATE SEQUENCE IF NOT EXISTS comment_sequence
--    START WITH 1
--    INCREMENT BY 1
--    CACHE 30;
--
--CREATE SEQUENCE IF NOT EXISTS booking_sequence
--    START WITH 1
--    INCREMENT BY 1
--    CACHE 30;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description VARCHAR(512) NOT NULL,
    requestor_id BIGINT,
    item_id BIGINT,
    created TIMESTAMP,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(requestor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    is_available BOOLEAN,
    owner_id BIGINT,
    request_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id),
    CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR(255),
    CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text VARCHAR(512) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP,
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id),
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id)
);