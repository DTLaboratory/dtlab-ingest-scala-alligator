# Digital Twin Lab Ingest - Alligator Version

## Under Construction

## Under Construction

## Under Construction

The Ingest Service accepts HTTP posts of JSON documents.  The JSON will
tend to be observations of the state of some thing in the world - a machine
temperature or a retail sale or a door opening or an approaching meteor's speed,
etc...

The raw data from these systems tends to be verbose and combines many
observations into a single JSON document.  This service decomposes the raw JSON
data into telemetry data - name, datetime, numerical value and forwards the
telemetry data in a universal DtLab format to DtLab actors.

The steps for defining a new ingest process are:

1. Define your types in the DtLab API - each value extracted from the incoming JSON must have a property defined in a DtType.
2. Define the object extractor if your incoming data contains arrays of objects - loading batches of time-series data.
3. Define the telemetry extractor
  * use the forward name specified in the object extractor
  * define groups of actor paths that will the extracted telemetry
  * define groups of properties to extract and send to the above paths
4. Post your JSON
  * possibly from a CRON cURL job polling NASA API hourly
  * possibly from a Kafka consumer
  * possibly from a MQTT consumer
