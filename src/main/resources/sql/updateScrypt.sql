CREATE TABLE fish_file
(
     id                 int NOT NULL AUTO_INCREMENT,
     fish_id            INT NOT NULL,
     file_name          VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (fish_id)
        REFERENCES fish(id)
);

INSERT INTO fish_file (fish_id, file_name)
SELECT id, image_file_name
FROM fish
WHERE image_file_name IS NOT NULL;

ALTER TABLE fish
    DROP COLUMN image_file_name;