#!/bin/bash

mvn exec:java -Dexec.mainClass="edu.ucdenver.ExecuteExperiment" -Dexec.args="$1 $2"
