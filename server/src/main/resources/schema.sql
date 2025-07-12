CREATE TABLE IF NOT EXISTS person (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS person_link (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    link VARCHAR(255) NOT NULL,
    nettsted VARCHAR(50),
    person_id BIGINT NOT NULL,
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kandidat_link (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    link VARCHAR(255) NOT NULL,
    nettsted VARCHAR(50),
    kandidat_id BIGINT NOT NULL,
    FOREIGN KEY (kandidat_id) REFERENCES kandidat_stortingsvalg(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kandidat_stortingsvalg (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valg VARCHAR(255),
    valgdistrikt VARCHAR(255),
    partikode VARCHAR(10),
    partinavn VARCHAR(255),
    display_order INT,
    kandidatnr INT,
    navn VARCHAR(255) NOT NULL,
    bosted VARCHAR(255),
    stilling VARCHAR(255),
    foedselsdato DATE,
    alder INT,
    kjoenn VARCHAR(10),
    INDEX idx_valgdistrikt (valgdistrikt),
    INDEX idx_partikode (partikode),
    INDEX idx_navn (navn)
);
