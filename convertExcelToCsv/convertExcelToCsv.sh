#!/bin/bash
for entry in "$search_dir"/home/rajeev/Share/impactFiles/*
do
	file=$(awk -F/ '{print $6}' <<< "$entry")
	outputFile=$(echo ${file%????}csv)

xlsx2csv -d , --dateformat %Y-%m-%d "$entry" "/home/rajeev/csv/$outputFile"

echo "$outputFile done"
done


