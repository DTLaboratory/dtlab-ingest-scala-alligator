# Digital Twin Lab Ingest - Alligator Version

## Under Construction

## Under Construction

## Under Construction

The Ingest Service accepts HTTP posts of JSON documents.  The JSON will
tend to be observations of the state of some thing in the world - a machine
temperature or a retail sale or a door opening, etc...

The raw JSON from these systems tends to be verbose and combinations of many
observations in a single JSON document.  This service decomposes the raw JSON
data into efficient telemetry data - name, datetime, numerical value and
forwards the telemetry data in this standard format to DtLab actors.

The steps for defining a new ingest endpoint are:

1. Define your types in the DtLab API - each value extracted from the incoming JSON must have a property name defined in a defined DtType.
2. Define the object extractor if your incoming data contains arrays of objects - loading batches of data.
3. Define the telemetry extractor using the forward name specified in the object extractor to define groups of actor paths that will receive groups of extracted telemetry. 
4. Post your JSON
