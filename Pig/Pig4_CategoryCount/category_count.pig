apps = LOAD '/PlayStoreProject/input/GooglePlay_200k.csv'
USING PigStorage(',')
AS (
app:chararray,
appid:chararray,
category:chararray,
rating:float,
ratingcount:int,
installs:chararray,
mininstalls:long,
maxinstalls:long,
free:chararray,
price:float,
currency:chararray,
size:chararray,
android:chararray,
developer:chararray,
website:chararray,
email:chararray,
released:chararray,
updated:chararray,
content:chararray,
privacy:chararray,
ads:chararray,
inapp:chararray,
editor:chararray,
scraped:chararray
);

-- Remove header
data = FILTER apps BY NOT (app == 'App Name');

-- Group by category
grp = GROUP data BY category;

-- Count apps in each category
result = FOREACH grp GENERATE group AS category, COUNT(data) AS total_apps;

-- Sort by highest count
ordered = ORDER result BY total_apps DESC;

STORE ordered INTO '/PlayStoreProject/output/Pig4_CategoryWiseAppCount'
USING PigStorage(',');