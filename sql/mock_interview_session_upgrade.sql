-- Upgrade the legacy mock_interview_session table without removing history.
-- The legacy voice-interview columns remain available for old records.

drop procedure if exists upgrade_mock_interview_session;
delimiter $$
create procedure upgrade_mock_interview_session()
begin
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'mock_interview_session' and column_name = 'resume_object_key') then
        alter table mock_interview_session add column resume_object_key varchar(512) null after student_id;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'mock_interview_session' and column_name = 'resume_file_name') then
        alter table mock_interview_session add column resume_file_name varchar(255) null after resume_object_key;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'mock_interview_session' and column_name = 'resume_text_snapshot') then
        alter table mock_interview_session add column resume_text_snapshot longtext null after resume_file_name;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'mock_interview_session' and column_name = 'profile_json') then
        alter table mock_interview_session add column profile_json longtext null after resume_text_snapshot;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'mock_interview_session' and column_name = 'profile_status') then
        alter table mock_interview_session add column profile_status varchar(32) not null default 'PROFILE_PROCESSING' after profile_json;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'mock_interview_session' and column_name = 'profile_error_message') then
        alter table mock_interview_session add column profile_error_message varchar(1000) null after profile_status;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'mock_interview_session' and column_name = 'profile_generated_at') then
        alter table mock_interview_session add column profile_generated_at datetime null after profile_error_message;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'mock_interview_session' and column_name = 'voice_session_id') then
        alter table mock_interview_session add column voice_session_id varchar(128) null after profile_generated_at;
    end if;
    if not exists (select 1 from information_schema.columns where table_schema = database() and table_name = 'mock_interview_session' and column_name = 'finished_at') then
        alter table mock_interview_session add column finished_at datetime null after started_at;
    end if;

    -- New sessions do not use these legacy required fields.
    alter table mock_interview_session
        modify column student_code varchar(64) null,
        modify column title varchar(100) null,
        modify column volc_session_id varchar(64) null;

    update mock_interview_session
       set voice_session_id = coalesce(voice_session_id, volc_session_id),
           finished_at = coalesce(finished_at, ended_at)
     where voice_session_id is null or finished_at is null;

    if not exists (select 1 from information_schema.statistics where table_schema = database() and table_name = 'mock_interview_session' and index_name = 'idx_mock_interview_student_created') then
        create index idx_mock_interview_student_created on mock_interview_session (student_id, created_at);
    end if;
    if not exists (select 1 from information_schema.statistics where table_schema = database() and table_name = 'mock_interview_session' and index_name = 'idx_mock_interview_voice_session') then
        create index idx_mock_interview_voice_session on mock_interview_session (voice_session_id);
    end if;
end $$
delimiter ;
call upgrade_mock_interview_session();
drop procedure if exists upgrade_mock_interview_session;
