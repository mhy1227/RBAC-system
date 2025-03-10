-- 创建密保信息表
CREATE TABLE sys_user_security (
    user_id BIGINT PRIMARY KEY COMMENT '用户ID',
    question1 VARCHAR(100) NOT NULL COMMENT '密保问题1',
    answer1 VARCHAR(200) NOT NULL COMMENT '密保答案1',
    question2 VARCHAR(100) NOT NULL COMMENT '密保问题2',
    answer2 VARCHAR(200) NOT NULL COMMENT '密保答案2',
    question3 VARCHAR(100) NOT NULL COMMENT '密保问题3',
    answer3 VARCHAR(200) NOT NULL COMMENT '密保答案3',
    error_count INT DEFAULT 0 COMMENT '答错次数',
    last_error_time DATETIME COMMENT '最后一次答错时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户密保信息表';

-- 添加用户表密保状态字段
ALTER TABLE sys_user
ADD COLUMN security_status TINYINT DEFAULT 0 COMMENT '密保状态(0-未设置 1-已设置)';

-- 创建密保问题推荐表
CREATE TABLE sys_security_question_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    question VARCHAR(100) NOT NULL COMMENT '问题内容',
    type INT DEFAULT 1 COMMENT '问题类型(1-常规 2-个人 3-工作)',
    status TINYINT DEFAULT 1 COMMENT '状态(0-禁用 1-启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_question (question)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密保问题模板表';

-- 插入一些默认的密保问题
INSERT INTO sys_security_question_template (question, type) VALUES 
('您母亲的姓名是？', 1),
('您父亲的姓名是？', 1),
('您配偶的姓名是？', 1),
('您的出生地是？', 1),
('您高中班主任的名字是？', 2),
('您的小学校名是？', 2),
('您最喜欢的电影是？', 3),
('您的第一个宠物的名字是？', 3),
('您最好的朋友名字是？', 3),
('您的初中毕业年份是？', 2); 