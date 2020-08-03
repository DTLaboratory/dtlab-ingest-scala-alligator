#!/usr/bin/env bash

curl -X POST --data-binary "@./examples/near_earth_objects.json" http://localhost:8082/dtlab-alligator/ingest/array/neo1

