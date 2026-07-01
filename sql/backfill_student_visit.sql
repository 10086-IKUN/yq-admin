-- Backfill pending visit plans for confirmed student tags.
-- Run once after deploying the visit-plan fix.
-- Existing pending visit plans are preserved.

INSERT INTO student_visit (
    student_id,
    teacher_id,
    next_visit_time,
    status
)
SELECT
    st.student_id,
    ec.head_teacher_id,
    CURDATE(),
    'PENDING'
FROM (
    SELECT st1.*
    FROM student_tag st1
    INNER JOIN (
        SELECT student_id, MAX(id) AS max_id
        FROM student_tag
        GROUP BY student_id
    ) latest ON latest.max_id = st1.id
) st
INNER JOIN edu_student es ON es.id = st.student_id
INNER JOIN edu_class ec ON ec.id = es.class_id
WHERE st.confirmed = 1
  AND ec.head_teacher_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM student_visit sv
      WHERE sv.student_id = st.student_id
        AND sv.status = 'PENDING'
  );
