--/======================================================================//
--/ (1) FUNCTION: gretapyta.QuestionnairesPopularityCount(boolean)
--/
-- CREATE OR REPLACE FUNCTION gretapyta."QuestionnairesPopularityCount"(
CREATE FUNCTION gretapyta."QuestionnairesPopularityCount"(
    orderbycnt boolean DEFAULT false)
  RETURNS TABLE(questionnaire_id integer, cntr integer)
  LANGUAGE 'sql'
AS '
SELECT a.questionnaire_id, count(1) counter
  FROM gretapyta.user_questionnaires a
  GROUP BY a.questionnaire_id
  ORDER BY
    (CASE WHEN orderbycnt THEN (-1 * count(1)) -- to get DESC order
     -- (CASE WHEN orderbycnt THEN cntr END) DESC;
    ELSE
      a.questionnaire_id
    END) ASC;
'
;

ALTER FUNCTION gretapyta."QuestionnairesPopularityCount"(boolean) OWNER TO postgres;

--/======================================================================//
--/ Test it:
-- select * from gretapyta."QuestionnairesPopularityCount"();
/*
--/ Result: ASC on questionnaire_id:
questionnaire_id  counter
----------------  -------
      1              3
      2              3
      5              3
*/

-- select * from gretapyta."QuestionnairesPopularityCount"(true);
/*
--/ Result: DESC on counter (popularity contest)
questionnaire_id  counter
----------------  -------
      5              3
      2              3
      1              3
*/

--/======================================================================//
--/ (2) FUNCTION: gretapyta.OptionsPopularityCount(integer, boolean)
--/
-- CREATE OR REPLACE FUNCTION gretapyta."OptionsPopularityCount"(
CREATE FUNCTION gretapyta."OptionsPopularityCount"(
    pquestion_id integer,
    orderbycnt boolean DEFAULT false)
  RETURNS TABLE(option_id integer, cntr integer)
  LANGUAGE 'sql'
AS '
 SELECT b.option_id, count(1) counter
   FROM gretapyta.answers_selected b
     WHERE b.question_answer_id IN (
          SELECT id
      FROM gretapyta.question_answers c
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

ALTER FUNCTION gretapyta."OptionsPopularityCount"(integer, boolean) OWNER TO postgres;
--/
--/======================================================================//

--/ Test it:
-- select * from gretapyta."OptionsPopularityCount"(1);
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

-- select * from gretapyta."OptionsPopularityCount"(1, true);
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