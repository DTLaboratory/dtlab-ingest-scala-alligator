# Digital Twin Lab Ingest - Alligator Version

![Scala CI](https://github.com/SoMind/dtlab-ingest-scala-alligator/workflows/Scala%20CI/badge.svg) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/fa9464cb51a441b1bb53122e2c8ac9f5)](https://app.codacy.com/gh/SoMind/dtlab-ingest-scala-alligator?utm_source=github.com&utm_medium=referral&utm_content=SoMind/dtlab-ingest-scala-alligator&utm_campaign=Badge_Grade_Dashboard)

![alt text](docs/logo_cropped.png)

The Ingest Service accepts HTTP posts of JSON documents.  The JSON will
tend to be observations of the state of some thing in the world -

  * a machine's
  * engine temperature
  * a retail sale transaction completion
  * the new availability of funds from a bank account transaction
  * a door opening
  * a motion detector triggered
  * an approaching asteroid's speed
  * etc...

The raw data announcing these events tends to be verbose and usually
combines multiple observations into a single JSON document.  The Dt Lab Ingest
Service decomposes this raw JSON data into telemetry data - name, datetime,
numerical value and forwards the telemetry data in a universal DtLab format to
DtLab actors.

The steps for defining a new ingest process are:

1. Define your types in the DtLab API - each value extracted from the incoming JSON must have a property defined in a DtType.
2. Define the object extractor if your incoming data contains arrays of objects - loading batches of time-series data.
3. Define the telemetry extractor
    * use the forward name specified in the object extractor
    * define groups of actor paths to the actors that will receive, remember, and act on the extracted telemetry
    * define groups of properties to extract and send to the above paths
4. Post your JSON
    * possibly from a CRON cURL job polling NASA API hourly
    * possibly from a Kafka consumer
    * possibly from a MQTT consumer

# GEN API DOCS

```
widdershins --environment reference/env.json reference/dtlab-ingest-scala-alligator.v1.yaml reference/README.md
```
