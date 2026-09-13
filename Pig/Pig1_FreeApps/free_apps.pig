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

-- Header remove
data = FILTER apps BY NOT (app == 'App Name');

-- Free apps
freeapps = FILTER data BY free == 'True';

STORE freeapps INTO '/PlayStoreProject/output/Pig1'
USING PigStorage(',');