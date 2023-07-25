--Question 1 Insert some data into a table

INSERT INTO cd.facilities (facid, name, membercost, guestcost,
                           initialoutlay, monthlymaintenance)
VALUES (9, 'Spa', 20, 30, 100000, 800);


--Question 2 Insert calculated data into a table

INSERT INTO cd.facilities (facid, name, membercost, guestcost,
                           initialoutlay, monthlymaintenance)
SELECT facid + 1,
       'Spa',
       20,
       30,
       100000,
       800
FROM cd.facilities
ORDER BY facid DESC
LIMIT 1;


--Question 3 Update some existing data

UPDATE
    cd.facilities
SET initialoutlay = 10000
WHERE facid = 1;


--Question 4 Update a row based on the contents of another row

UPDATE
    cd.facilities
SET membercost =(SELECT membercost * 1.1
                 FROM cd.facilities
                 WHERE facid = 0),
    guestcost  =(SELECT guestcost * 1.1
                 FROM cd.facilities
                 WHERE facid = 0)
WHERE facid = 1;

--Question 5 Delete all bookings

DELETE
FROM cd.bookings;

--Question 6 Delete a member from the cd.members table

DELETE
FROM cd.members
WHERE memid = 37;

--Question 7 Control which rows are retrieved - part 2

SELECT facid,
       name,
       membercost,
       monthlymaintenance
FROM cd.facilities
WHERE membercost < (monthlymaintenance / 50)
  AND membercost > 0;

--Question 8 Basic string searches

SELECT *
FROM cd.facilities
WHERE name ILIKE '%Tennis%';


--Question 9 Matching against multiple possible values

SELECT *
FROM cd.facilities
WHERE facid IN (1, 5);


--Question 10 Working with dates

SELECT memid,
       surname,
       firstname,
       joindate
FROM cd.members
WHERE joindate > '2012-08-31 11:59:59';


--Question 11 Combining results from multiple queries

SELECT surname
FROM cd.members
UNION
SELECT name
FROM cd.facilities;

--Question 12 Retrieve the start times of members' bookings

SELECT DISTINCT starttime
FROM cd.bookings
         INNER JOIN cd.members ON cd.bookings.memid = (SELECT memid
                                                       FROM cd.members
                                                       WHERE firstname = 'David'
                                                         AND surname = 'Farrell');

--Question 13 Work out the start times of bookings for tennis courts

SELECT cd.bookings.starttime AS start,
       cd.facilities.name    AS name
FROM cd.bookings
         JOIN cd.facilities ON cd.bookings.facid = cd.facilities.facid
WHERE DATE(cd.bookings.starttime) = '2012-09-21'
  AND cd.facilities.name ILIKE 'Tennis%'
ORDER BY start ASC;

--Question 14 Produce a list of all members, along with their recommender

SELECT c1.firstname AS memfname,
       c1.surname   AS memsname,
       c2.firstname AS recfname,
       c2.surname   AS recsname
FROM cd.members AS c1
         LEFT JOIN cd.members AS c2 ON c1.recommendedBy = c2.memid
ORDER BY memsname,
         memfname;

--Question 15 Produce a list of all members who have recommended another member

SELECT DISTINCT c2.firstname AS firstname,
                c2.surname   AS surname
FROM cd.members AS c1
         JOIN cd.members AS c2 ON c1.recommendedBy = c2.memid
ORDER BY surname,
         firstname;

--Question 16 Produce a list of all members, along with their recommender, using no joins.

SELECT DISTINCT mems.firstname || ' ' || mems.surname AS member,
                (SELECT recs.firstname || ' ' || recs.surname AS recommender
                 FROM cd.members recs
                 WHERE recs.memid = mems.recommendedby)
FROM cd.members mems
ORDER BY member;


--Question 17 Count the number of recommendations each member makes.

SELECT mems.memid AS recommendedby,
       COUNT(mems.memid)
FROM cd.members mems
         INNER JOIN cd.members recs ON mems.memid = recs.recommendedby
GROUP BY mems.memid
ORDER BY recommendedby;

--Question 18 List the total slots booked per facility

SELECT fac.facid       AS facid,
       SUM(book.slots) AS "Total Slots"
FROM cd.facilities fac
         INNER JOIN cd.bookings book ON fac.facid = book.facid
GROUP BY fac.facid
ORDER BY facid;

--Question 19 List the total slots booked per facility in a given month

SELECT fac.facid       as facid,
       SUM(book.slots) as "Total Slots"
FROM cd.facilities fac
         INNER JOIN cd.bookings book ON fac.facid = book.facid
WHERE DATE(book.starttime) > '2012-08-31'
  AND DATE(book.starttime) < '2012-10-01'
GROUP BY fac.facid
ORDER BY "Total Slots";

--Question 20 List the total slots booked per facility per month

SELECT fac.facid AS facid,
       EXTRACT(
               MONTH
               FROM
               book.starttime
           )     AS month,
       SUM(book.slots)
FROM cd.facilities fac
         INNER JOIN cd.bookings book ON fac.facid = book.facid
WHERE DATE(book.starttime) >= '2012-01-01'
  AND DATE(book.starttime) <= '2012-12-31'
GROUP BY fac.facid,
         month
ORDER BY facid,
         month;

--Question 21 Find the count of members who have made at least one booking

SELECT COUNT(DISTINCT mems.memid)
FROM cd.members mems
         INNER JOIN cd.bookings book ON mems.memid = book.memid;


-- Question 22 List each member's first booking after September 1st 2012

SELECT mems.surname,
       mems.firstname,
       mems.memid,
       MIN(book.starttime)
FROM cd.members mems
         INNER JOIN cd.bookings book ON mems.memid = book.memid
WHERE DATE(book.starttime) >= '2012-09-01'
GROUP BY mems.memid
ORDER BY mems.memid;


--Question 23 Produce a list of member names, with each row containing the total member count

SELECT count(*) over (),
       firstname,
       surname
FROM cd.members
ORDER BY joindate;

--Question 24 Produce a numbered list of members

SELECT COUNT(memid) OVER (
    ORDER BY
        joindate
    ),
       firstname,
       surname
FROM cd.members
ORDER BY memid;

--Question 25 Output the facility id that has the highest number of slots booked, again

SELECT facid,
       total
FROM (SELECT fac.facid,
             SUM(book.slots) AS total,
             RANK() OVER (
                 ORDER BY
                     SUM(book.slots) DESC
                 )           AS rank
      FROM cd.facilities fac
               INNER JOIN cd.bookings book ON fac.facid = book.facid
      GROUP BY fac.facid) ranked_totals
WHERE rank = 1;

--Question 26 Format the names of members

SELECT (surname || ', ' || firstname) AS name
FROM cd.members;

--Question 27 Find telephone numbers with parentheses


SELECT memid,
       telephone
FROM cd.members
WHERE telephone LIKE '(___)%'
ORDER BY memid;

--Question 28 Count the number of members whose surname starts with each letter of the alphabet

SELECT SUBSTRING(surname, 1, 1) AS letter,
       COUNT(memid)
FROM cd.members
GROUP BY letter
ORDER BY letter;









