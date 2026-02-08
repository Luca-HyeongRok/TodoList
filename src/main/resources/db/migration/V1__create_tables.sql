CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(191) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS list_tb (
    list_id INT NOT NULL AUTO_INCREMENT,
    content VARCHAR(255),
    priority INT NOT NULL,
    start_date DATETIME,
    end_date DATETIME,
    done BIT(1) NOT NULL,
    user_id VARCHAR(191) NOT NULL,
    PRIMARY KEY (list_id),
    CONSTRAINT fk_list_tb_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
