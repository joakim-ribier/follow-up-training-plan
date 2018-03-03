#!/bin/bash +x

jar=$(find -name Follow-UpT.Plan-assembly*.jar)
scala $jar
