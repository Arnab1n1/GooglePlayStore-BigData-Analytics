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

data = FILTER apps BY NOT (app == 'App Name');

paid = FILTER data BY free == 'False' AND price > 5;

STORE paid INTO '/PlayStoreProject/output/Pig2_PaidAppsPriceAbove5'
USING PigStorage(',');