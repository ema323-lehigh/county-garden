#!/usr/bin/env bash

cat /dev/null > copythis.txt

echo "DELETE employee;" >> copythis.txt
cat employees.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE agent;" >> copythis.txt
cat agents.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE adjuster;" >> copythis.txt
cat adjusters.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE customer;" >> copythis.txt
cat customers.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE dependentt;" >> copythis.txt
cat dependents.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE polisy;" >> copythis.txt
cat policies.txt >> copythis.txt
echo -e '' >> copythis.txt
