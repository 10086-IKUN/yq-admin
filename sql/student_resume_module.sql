-- 学员简历模块：文件元数据、解析文本和解析状态。
-- 使用存储过程保证重复执行不会重复添加字段。
delimiter $$
drop procedure if exists add_student_resume_columns $$
create procedure add_student_resume_columns()
begin
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'edu_student' and column_name = 'resume_object_key') then
        alter table edu_student add column resume_object_key varchar(500) null comment '简历OSS ObjectKey' after join_time;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'edu_student' and column_name = 'resume_file_name') then
        alter table edu_student add column resume_file_name varchar(255) null comment '简历文件名' after resume_object_key;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'edu_student' and column_name = 'resume_file_size') then
        alter table edu_student add column resume_file_size bigint null comment '简历文件大小' after resume_file_name;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'edu_student' and column_name = 'resume_uploaded_at') then
        alter table edu_student add column resume_uploaded_at datetime null comment '简历上传时间' after resume_file_size;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'edu_student' and column_name = 'resume_text') then
        alter table edu_student add column resume_text longtext null comment '简历解析文本' after resume_uploaded_at;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'edu_student' and column_name = 'resume_parse_status') then
        alter table edu_student add column resume_parse_status varchar(32) null comment '解析状态：PENDING/PROCESSING/SUCCESS/FAILED' after resume_text;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'edu_student' and column_name = 'resume_parse_error_message') then
        alter table edu_student add column resume_parse_error_message varchar(1000) null comment '简历解析失败原因' after resume_parse_status;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'edu_student' and column_name = 'resume_parsed_at') then
        alter table edu_student add column resume_parsed_at datetime null comment '简历解析完成时间' after resume_parse_error_message;
    end if;
end $$
call add_student_resume_columns() $$
drop procedure add_student_resume_columns $$
delimiter ;
