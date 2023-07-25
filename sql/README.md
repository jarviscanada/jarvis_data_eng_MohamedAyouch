# Introduction

# SQL queries

###### Table Setup (DDL)

```sql
CREATE TABLE cd.members (
  memid integer NOT NULL,
  surname character varying(200) NOT NULL,
  firstname character varying(200) NOT NULL,
  address character varying(300) NOT NULL,
  zipcode integer NOT NULL,
  telephone character varying(20) NOT NULL,
  recommendedby integer,
  joindate timestamp NOT NULL,
  CONSTRAINT members_pk PRIMARY KEY (memid),
  CONSTRAINT  fk_members_recommendedby FOREIGN KEY (recommendedby) REFERENCES cd.members(memid) ON DELETE SET NULL
  );

CREATE TABLE cd.facilities
(
    facid integer NOT NULL,
    name character varying(100) NOT NULL,
    membercost numeric NOT NULL,
    guestcost numeric NOT NULL,
    initialoutlay numeric NOT NULL,
    monthlymaintenance numeric NOT NULL,
    CONSTRAINT facilities_pk PRIMARY KEY (facid)
);

CREATE TABLE cd.bookings (
  bookid integer NOT NULL, 
  facid integer NOT NULL, 
  memid integer NOT NULL, 
  starttime timestamp NOT NULL, 
  slots integer NOT NULL, 
  CONSTRAINT bookings_pk PRIMARY KEY (bookid), 
  CONSTRAINT fk_bookings_facid FOREIGN KEY (facid) REFERENCES cd.facilities(facid), 
  CONSTRAINT fk_bookings_memid FOREIGN KEY (memid) REFERENCES cd.members(memid)
);
```

###### Question 1: Show all members 

```sql
INSERT INTO cd.facilities (
    facid, name, membercost, guestcost,
    initialoutlay, monthlymaintenance
)
VALUES
    (9, 'Spa', 20, 30, 100000, 800);

```

###### Question 2: Insert calculated data into a table

```sql
INSERT INTO cd.facilities (
    facid, name, membercost, guestcost,
    initialoutlay, monthlymaintenance
)
SELECT
    facid + 1,
    'Spa',
    20,
    30,
    100000,
    800
FROM
    cd.facilities
ORDER BY
    facid DESC
LIMIT
    1;

```

###### Question 3: Update some existing data

```sql
UPDATE
    cd.facilities
SET
    initialoutlay = 10000
WHERE
    facid = 1;

```

###### Question 4: Update a row based on the contents of another row

```sql
UPDATE 
  cd.facilities 
SET 
  membercost =(
    SELECT 
      membercost * 1.1 
    FROM 
      cd.facilities 
    WHERE 
      facid = 0
  ), 
  guestcost =(
    SELECT 
      guestcost * 1.1 
    FROM 
      cd.facilities 
    WHERE 
      facid = 0
  ) 
WHERE 
  facid = 1;
```

###### Question 5: Delete all bookings

```sql
DELETE FROM cd.bookings;
```

###### Question 6: Delete a member from the cd.members table

```sql
DELETE FROM 
  cd.members 
WHERE 
  memid = 37;
```

###### Question 7: Control which rows are retrieved - part 2

```sql
SELECT 
  facid, 
  name, 
  membercost, 
  monthlymaintenance 
FROM 
  cd.facilities 
WHERE 
  membercost <(monthlymaintenance / 50) 
  AND membercost > 0;
```

###### Question 8: Basic string searches

```sql
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  name ILIKE '%Tennis%';
```

###### Queston 9: Matching against multiple possible values

```sql
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  facid IN (1, 5);
```

###### Queston 10: Working with dates

```sql
SELECT 
  memid, 
  surname, 
  firstname, 
  joindate 
FROM 
  cd.members 
WHERE 
  joindate > '2012-08-31 11:59:59';
```

##### Question 11: Combining results from multiple queries

```sql
SELECT 
  surname 
FROM 
  cd.members 
UNION 
SELECT 
  name 
FROM 
  cd.facilities;
```


##### Question 12: Combining results from multiple queries

```sql
SELECT 
  DISTINCT starttime 
FROM 
  cd.bookings 
  INNER JOIN cd.members ON cd.bookings.memid =(
    SELECT 
      memid 
    FROM 
      cd.members 
    WHERE 
      firstname = 'David' 
      AND surname = 'Farrell'
  );
```

##### Question 13: Work out the start times of bookings for tennis courts

```sql
SELECT 
  cd.bookings.starttime AS start, 
  cd.facilities.name AS name 
FROM 
  cd.bookings 
  JOIN cd.facilities ON cd.bookings.facid = cd.facilities.facid 
WHERE 
  DATE(cd.bookings.starttime) = '2012-09-21' 
  AND cd.facilities.name ILIKE 'Tennis%' 
ORDER BY 
  start ASC;
```

##### Question 14: Produce a list of all members, along with their recommender

```sql
SELECT 
  c1.firstname AS memfname, 
  c1.surname AS memsname, 
  c2.firstname AS recfname, 
  c2.surname AS recsname 
FROM 
  cd.members AS c1 
  LEFT JOIN cd.members AS c2 ON c1.recommendedBy = c2.memid 
ORDER BY 
  memsname, 
  memfname;
```

##### Question 15: Produce a list of all members who have recommended another member

```sql
SELECT 
  DISTINCT c2.firstname AS firstname, 
  c2.surname AS surname 
FROM 
  cd.members AS c1 
  JOIN cd.members AS c2 ON c1.recommendedBy = c2.memid 
ORDER BY 
  surname, 
  firstname;
```

##### Question 16: Produce a list of all members, along with their recommender, using no joins.

```sql
SELECT DISTINCT mems.firstname || ' ' || mems.surname AS member,(
  SELECT recs.firstname || ' ' || recs.surname AS recommender
  FROM cd.members recs
  WHERE recs.memid = mems.recommendedby)
FROM cd.members mems
ORDER BY member;
```

##### Question 17: Count the number of recommendations each member makes.

```sql
SELECT 
  mems.memid AS recommendedby, 
  COUNT(mems.memid) 
FROM 
  cd.members mems 
  INNER JOIN cd.members recs ON mems.memid = recs.recommendedby 
GROUP BY 
  mems.memid 
ORDER BY 
  recommendedby;
```

##### Question 18: List the total slots booked per facility

```sql
SELECT 
  fac.facid AS facid, 
  SUM(book.slots) AS "Total Slots" 
FROM 
  cd.facilities fac 
  INNER JOIN cd.bookings book ON fac.facid = book.facid 
GROUP BY 
  fac.facid 
ORDER BY 
  facid;
```

##### Question 19: List the total slots booked per facility in a given month

```sql
SELECT 
  fac.facid as facid, 
  SUM(book.slots) as "Total Slots" 
FROM 
  cd.facilities fac 
  INNER JOIN cd.bookings book ON fac.facid = book.facid 
WHERE 
  DATE(book.starttime) > '2012-08-31' 
  AND DATE(book.starttime) < '2012-10-01' 
GROUP BY 
  fac.facid 
ORDER BY 
  "Total Slots";
```

##### Question 20: List the total slots booked per facility per month

```sql
SELECT 
  fac.facid AS facid, 
  EXTRACT(
    MONTH 
    FROM 
      book.starttime
  ) AS month, 
  SUM(book.slots) 
FROM 
  cd.facilities fac 
  INNER JOIN cd.bookings book ON fac.facid = book.facid 
WHERE 
  DATE(book.starttime) >= '2012-01-01' 
  AND DATE(book.starttime) <= '2012-12-31' 
GROUP BY 
  fac.facid, 
  month 
ORDER BY 
  facid, 
  month;
```

##### Question 21: Find the count of members who have made at least one booking

```sql
SELECT 
  COUNT (DISTINCT mems.memid) 
FROM 
  cd.members mems 
  INNER JOIN cd.bookings book ON mems.memid = book.memid;
```

##### Question 22: List each member's first booking after September 1st 2012

```sql
SELECT 
  mems.surname, 
  mems.firstname, 
  mems.memid, 
  MIN(book.starttime) 
FROM 
  cd.members mems 
  INNER JOIN cd.bookings book ON mems.memid = book.memid 
WHERE 
  DATE(book.starttime) >= '2012-09-01' 
GROUP BY 
  mems.memid 
ORDER BY 
  mems.memid;
```

##### Question 23: Produce a list of member names, with each row containing the total member count

```sql
SELECT
    count(*) over (),
    firstname,
    surname
FROM
    cd.members
ORDER BY
    joindate;
```

##### Question 24: Produce a numbered list of members

```sql
SELECT 
  COUNT(memid) OVER(
    ORDER BY 
      joindate
  ), 
  firstname, 
  surname 
FROM 
  cd.members 
ORDER BY 
  memid;
```

##### Question 25: Output the facility id that has the highest number of slots booked, again

```sql
SELECT 
  facid, 
  total 
FROM 
  (
    SELECT 
      fac.facid, 
      SUM(book.slots) AS total, 
      RANK() OVER (
        ORDER BY 
          SUM(book.slots) DESC
      ) AS rank 
    FROM 
      cd.facilities fac 
      INNER JOIN cd.bookings book ON fac.facid = book.facid 
    GROUP BY 
      fac.facid
  ) ranked_totals 
WHERE 
  rank = 1;
```

##### Question 26: Format the names of members

```sql
SELECT
(surname || ', ' || firstname) AS name
FROM
cd.members;
```

#####  Question 27: Find telephone numbers with parentheses

```sql
SELECT 
  memid, 
  telephone 
FROM 
  cd.members 
WHERE 
  telephone LIKE '(___)%' 
ORDER BY 
  memid;
```

#####  Question 28: Count the number of members whose surname starts with each letter of the alphabet

```sql
SELECT 
  SUBSTRING(surname, 1, 1) AS letter, 
  COUNT(memid) 
FROM 
  cd.members 
GROUP BY 
  letter 
ORDER BY 
  letter;
```






