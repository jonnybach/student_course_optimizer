#!/bin/bash

if [ "$DB_OPTIONS" = "" ]; then
    DB_OPTIONS="--opt -u project3ag --password=cs6310p3ag"
fi

TS=`date +%g%m%d`

mysqldump $DB_OPTIONS project3ag > project3ag_${TS}.mysql
