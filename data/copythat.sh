#!/usr/bin/env bash

cat /dev/null > copythis.txt

echo "DELETE employee;" >> copythis.txt
cat output/employees.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE agent;" >> copythis.txt
cat output/agents.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE adjuster;" >> copythis.txt
cat output/adjusters.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE customer;" >> copythis.txt
cat output/customers.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE cust_add;" >> copythis.txt
cat output/addresses.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE dependentt;" >> copythis.txt
cat output/dependents.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE polisy;" >> copythis.txt
cat output/policies.txt >> copythis.txt
echo -e '' >> copythis.txt
echo "DELETE item;" >> copythis.txt
cat output/items.txt >> copythis.txt
echo -e '' >> copythis.txt
