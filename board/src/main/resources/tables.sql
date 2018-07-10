-- 테이블 순서는 관계를 고려하여 한 번에 실행해도 에러가 발생하지 않게 정렬되었습니다.

-- users Table Create SQL

CREATE TABLE users

(
    `user_id`       BIGINT           NOT NULL    AUTO_INCREMENT, 
    `email`         VARCHAR(255)     NOT NULL, 
    `encoded_pw`    VARCHAR(255)     NOT NULL, 
    `name`          VARCHAR(100)     NOT NULL, 
    `phone`         VARCHAR(20)      NOT NULL, 
    `company`       VARCHAR(255)     NULL, 
    `introduction`  VARCHAR(1000)    NULL, 
    `join_date`     DATETIME         NOT NULL, 
    PRIMARY KEY (user_id),
    UNIQUE KEY (email)
) engine=innodb, charset=utf8 ;

 

-- groups Table Create SQL

CREATE TABLE groups

(
    `group_id`                    BIGINT           NOT NULL AUTO_INCREMENT, 
    `user_id`                     BIGINT           NOT NULL, 
    `img_addr`                    VARCHAR(1000)    NULL, 
    `title`                       VARCHAR(1000)    NOT NULL, 
    `recruiting_policy`           VARCHAR(45)      NOT NULL, 
    `project_start_datetime`      DATETIME         NOT NULL, 
    `project_finish_datetime`     DATETIME         NOT NULL, 
    `recruiting_start_datetime`   DATETIME         NOT NULL, 
    `recruiting_finish_datetime`  DATETIME         NOT NULL, 
    `personnel`                   INT              NOT NULL, 
    `category`                    VARCHAR(100)     NOT NULL, 
    `address`                     VARCHAR(1000)    NOT NULL, 
    `address_do` VARCHAR(20)	   NOT NULL,
    `simple_info`                 VARCHAR(100)     NOT NULL, 
    `detail_info`                 VARCHAR(2000)    NULL, 
    `create_time`                 DATETIME         NOT NULL, 
    `modify_time`                 DATETIME         NOT NULL, 
    PRIMARY KEY (group_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)  ON DELETE cascade,
    INDEX (category),
    INDEX (address_do)
)engine=innodb, charset=utf8 ;

 

-- applys Table Create SQL

CREATE TABLE applys

(
    `apply_id`     BIGINT      NOT NULL    AUTO_INCREMENT, 
    `user_id`      BIGINT      NOT NULL, 
    `group_id`     BIGINT      NOT NULL, 
    `apply_time`   DATETIME    NOT NULL, 
    PRIMARY KEY (apply_id),
    FOREIGN KEY (group_id) REFERENCES groups (group_id)  ON DELETE cascade,
    FOREIGN KEY (user_id) REFERENCES users (user_id)  ON DELETE cascade,
    UNIQUE KEY (user_id, group_id)
)engine=innodb, charset=utf8 ;

 

 

-- comments Table Create SQL

CREATE TABLE comments

(
    `comment_id`   BIGINT           NOT NULL    AUTO_INCREMENT, 
    `user_id`      BIGINT           NOT NULL, 
    `group_id`     BIGINT           NOT NULL, 
    `content`      VARCHAR(1000)    NOT NULL, 
    `create_time`  DATETIME         NOT NULL, 
    PRIMARY KEY (comment_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)  ON DELETE cascade,
    FOREIGN KEY (group_id) REFERENCES groups (group_id)  ON DELETE cascade
)engine=innodb, charset=utf8 ;

 

 

-- recommends Table Create SQL

CREATE TABLE recommends

(
    `recommend_id`  BIGINT    NOT NULL    AUTO_INCREMENT, 
    `user_id`       BIGINT    NOT NULL, 
    `group_id`      BIGINT    NOT NULL, 
    PRIMARY KEY (recommend_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)  ON DELETE cascade,
    FOREIGN KEY (group_id) REFERENCES groups (group_id)  ON DELETE cascade,
    UNIQUE KEY (user_id, group_id)
)engine=innodb, charset=utf8 ;

 