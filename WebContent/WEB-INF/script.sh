#!/bin/bash
# Shell script to run sql file
###########################################################

Main() {
/usr/lib/oracle/xe/app/oracle/product/10.2.0/server/config/scripts/sqlplus.sh -s << EOF
CONNECT kernel32/v0qV0nsktr;

set termout off
set pages 999
SET MARKUP HTML ON TABLE "class=mui-table--bordered border=1px solid black" ENTMAP OFF

spool /home/oracle/sqlfiles/result.html


@/home/oracle/sqlfiles/query.sql

spool off;

exit;
EOF
}

Main | tee /home/oracle/sqlfiles/autosql.log