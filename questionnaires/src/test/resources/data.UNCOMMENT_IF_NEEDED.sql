--/======================================================================//
--/ (1) FUNCTION: gretapyta-test.QuestionnairesPopularityCount(boolean)
--/
-- CREATE OR REPLACE FUNCTION "gretapyta-test"."QuestionnairesPopularityCount"(
CREATE FUNCTION "gretapyta-test"."QuestionnairesPopularityCount"(
    -- pquestion_id integer,
    orderbycnt boolean DEFAULT false)
  RETURNS TABLE(questionnaire_id integer, cntr integer)
  LANGUAGE 'sql'
--    COST 100
--    VOLATILE PARALLEL UNSAFE
--    ROWS 1000
AS '
SELECT a.questionnaire_id, count(1) counter
  FROM "gretapyta-test".user_questionnaires a
  GROUP BY a.questionnaire_id
  ORDER BY
    (CASE WHEN orderbycnt THEN (-1 * count(1)) -- to get DESC order
     -- (CASE WHEN orderbycnt THEN cntr END) DESC;
    ELSE
      a.questionnaire_id
    END) ASC;
'
;

ALTER FUNCTION "gretapyta-test"."QuestionnairesPopularityCount"(boolean) OWNER TO postgres;

--/////////////////////////////////////////////////////////////////////////--
/* -- NOT WORKING:
DO
'
Begin
IF NOT EXISTS
(SELECT count(1) FROM information_schema.routines
                 WHERE routine_schema = ''gretapyta''
                   AND routine_name = ''QuestionnairesPopularityCount'')

 THEN

--===========================
CREATE FUNCTION gretapyta."QuestionnairesPopularityCount"(
    -- pquestion_id integer,
    orderbycnt boolean DEFAULT false)
  RETURNS TABLE(questionnaire_id integer, cntr integer)
--    COST 100
--    VOLATILE PARALLEL UNSAFE
--    ROWS 1000
AS ''
BEGIN

SELECT a.questionnaire_id, count(1) counter
  FROM gretapyta.user_questionnaires a
  GROUP BY a.questionnaire_id
  ORDER BY
    (CASE WHEN orderbycnt THEN (-1 * count(1)) -- to get DESC order
     -- (CASE WHEN orderbycnt THEN cntr END) DESC;
    ELSE
      a.questionnaire_id
    END) ASC;

END;
''
language plpgsql;

ALTER FUNCTION gretapyta."QuestionnairesPopularityCount"(boolean) OWNER TO postgres;
--===========================

END IF;
end;
'
language plpgsql;
*/
--/////////////////////////////////////////////////////////////////////////--

--/
--/======================================================================//
--/ Test it:
-- select * from "gretapyta-test"."QuestionnairesPopularityCount"();
/*
--/ Result: ASC on questionnaire_id:
questionnaire_id  counter
----------------  -------
      1              3
      2              3
      5              3
*/

-- select * from "gretapyta-test"."QuestionnairesPopularityCount"(true);
/*
--/ Result: DESC on counter (popularity contest)
questionnaire_id  counter
----------------  -------
      5              3
      2              3
      1              3
*/

--/======================================================================//
--/ (2) FUNCTION: gretapyta-test.OptionsPopularityCount(integer, boolean)
--/
-- CREATE OR REPLACE FUNCTION "gretapyta-test"."OptionsPopularityCount"(
CREATE FUNCTION "gretapyta-test"."OptionsPopularityCount"(
    pquestion_id integer,
    orderbycnt boolean DEFAULT false)
  RETURNS TABLE(option_id integer, cntr integer)
  LANGUAGE 'sql'
--    COST 100
--    VOLATILE PARALLEL UNSAFE
--    ROWS 1000

AS '
 SELECT b.option_id, count(1) counter
   FROM "gretapyta-test".answers_selected b
     WHERE b.question_answer_id IN (
          SELECT id
      FROM "gretapyta-test".question_answers c
           WHERE c.question_id=pQuestion_id)
  GROUP BY b.option_id
  ORDER BY
    (CASE WHEN orderbycnt THEN (-1 * count(1)) -- to get DESC order
     -- (CASE WHEN orderbycnt THEN cntr END) DESC;
    ELSE
      b.option_id
    END) ASC;
'
;

ALTER FUNCTION "gretapyta-test"."OptionsPopularityCount"(integer, boolean) OWNER TO postgres;
--/
--/======================================================================//

--/ Test it:
-- select * from "gretapyta-test"."OptionsPopularityCount"(1);
/*
--/ Result: ASC on option_id:
option_id  counter
---------  -------
   11         1
   12         2
   13         1
   14         1
   15         1
*/

-- select * from "gretapyta-test"."OptionsPopularityCount"(1, true);
/*
--/ Result: DESC on counter (popularity contest)
option_id  counter
---------  -------
   12         2
   11         1
   13         1
   14         1
   15         1
*/