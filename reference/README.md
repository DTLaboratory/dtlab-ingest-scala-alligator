---
title: dtlab ingest alligator v1.0
language_tabs:
  - python: Python
  - shell: Shell
  - javascript: Javascript
  - java: Java
toc_footers: []
includes: []
search: true
highlight_theme: darkula
headingLevel: 2

---

<!-- Generator: Widdershins v4.0.1 -->

<h1 id="dtlab-ingest-alligator">dtlab ingest alligator v1.0</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

Manage DtLab Telemetry Ingest Service.

DtLab Ingest is an streaming service that feeds the DtLab actor system updates from the field.

Base URLs:

* <a href="http://localhost:8082">http://localhost:8082</a>

Email: <a href="mailto:ed@onextent.com">navicore</a> 
License: <a href="https://github.com/SoMind/dtlab-ingest-scala-alligator/blob/master/LICENSE">MIT</a>

<h1 id="dtlab-ingest-alligator-default">Default</h1>

## delete-dtlab-ingest-alligator-extractor-telemetry-spec

<a id="opIddelete-dtlab-ingest-alligator-extractor-telemetry-spec"></a>

> Code samples

```python
import requests

r = requests.delete('http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}')

print(r.json())

```

```shell
# You can also use wget
curl -X DELETE http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}

```

```javascript

fetch('http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}',
{
  method: 'DELETE'

})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`DELETE /dtlab-ingest-alligator/extractor/telemetry/{specId}`

*delete telemetry extraction spec*

Delete the spec.

<h3 id="delete-dtlab-ingest-alligator-extractor-telemetry-spec-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|specId|path|string|true|none|

<h3 id="delete-dtlab-ingest-alligator-extractor-telemetry-spec-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|202|[Accepted](https://tools.ietf.org/html/rfc7231#section-6.3.3)|Accepted|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|None|

<aside class="success">
This operation does not require authentication
</aside>

## get-dtlab-ingest-alligator-extractor-object-spec

<a id="opIdget-dtlab-ingest-alligator-extractor-object-spec"></a>

> Code samples

```python
import requests

r = requests.get('http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}')

print(r.json())

```

```shell
# You can also use wget
curl -X GET http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}

```

```javascript

fetch('http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}',
{
  method: 'GET'

})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /dtlab-ingest-alligator/extractor/object/{specId}`

*get object extractor spec*

Instructions for extracting an array of telemetry from a single batch of telemetry packaged in a single json object.

<h3 id="get-dtlab-ingest-alligator-extractor-object-spec-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|specId|path|string|true|none|

<h3 id="get-dtlab-ingest-alligator-extractor-object-spec-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|

<aside class="success">
This operation does not require authentication
</aside>

## delete-dtlab-ingest-alligator-extractor-object-spec

<a id="opIddelete-dtlab-ingest-alligator-extractor-object-spec"></a>

> Code samples

```python
import requests

r = requests.delete('http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}')

print(r.json())

```

```shell
# You can also use wget
curl -X DELETE http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}

```

```javascript

fetch('http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}',
{
  method: 'DELETE'

})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`DELETE /dtlab-ingest-alligator/extractor/object/{specId}`

delete the object extraction spec

<h3 id="delete-dtlab-ingest-alligator-extractor-object-spec-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|specId|path|string|true|none|

<h3 id="delete-dtlab-ingest-alligator-extractor-object-spec-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|202|[Accepted](https://tools.ietf.org/html/rfc7231#section-6.3.3)|Accepted|None|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|None|

<aside class="success">
This operation does not require authentication
</aside>

## post-dtlab-ingest-alligator-extractor-object-specId

<a id="opIdpost-dtlab-ingest-alligator-extractor-object-specId"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
}

r = requests.post('http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}', headers = headers)

print(r.json())

```

```shell
# You can also use wget
curl -X POST http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId} \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'

```

```javascript
const inputBody = '{
  "path": "$.near_earth_objects.*[*]",
  "telSpecId": "neo1"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json'
};

fetch('http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8082/dtlab-ingest-alligator/extractor/object/{specId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`POST /dtlab-ingest-alligator/extractor/object/{specId}`

Create object extraction spec.

> Body parameter

```json
{
  "path": "$.near_earth_objects.*[*]",
  "telSpecId": "neo1"
}
```

<h3 id="post-dtlab-ingest-alligator-extractor-object-specid-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[ObjectExtractorSpec](#schemaobjectextractorspec)|false|none|
|specId|path|string|true|none|

> Example responses

> Created

```json
{
  "created": "2020-08-24T17:32:34.766Z",
  "path": "$.near_earth_objects.*[*]",
  "specId": "neo1",
  "telSpecId": "neo1"
}
```

<h3 id="post-dtlab-ingest-alligator-extractor-object-specid-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|Created|[ObjectExtractorSpec](#schemaobjectextractorspec)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict|None|

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="dtlab-ingest-alligator-ask">ask</h1>

## get-dtlab-ingest-alligator-extractor-telemetry-spec

<a id="opIdget-dtlab-ingest-alligator-extractor-telemetry-spec"></a>

> Code samples

```python
import requests
headers = {
  'Accept': 'application/json'
}

r = requests.get('http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}', headers = headers)

print(r.json())

```

```shell
# You can also use wget
curl -X GET http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId} \
  -H 'Accept: application/json'

```

```javascript

const headers = {
  'Accept':'application/json'
};

fetch('http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /dtlab-ingest-alligator/extractor/telemetry/{specId}`

*get telemetry extraction spec*

Look up a telemetry extraction spec by ID.

<h3 id="get-dtlab-ingest-alligator-extractor-telemetry-spec-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|specId|path|string|true|none|

> Example responses

> Definition of the telemetry extraction spec.

```json
[
  {
    "specId": "neo1",
    "created": "2020-08-23T21:39:14.291Z",
    "datetimeFmt": "yyyy-MMM-dd hh:mm",
    "datetimePath": "$.close_approach_data[0].close_approach_date_full",
    "paths": [
      [
        {
          "name": "orbiting_body",
          "path": "$.close_approach_data[0].orbiting_body",
          "valueType": "String"
        },
        {
          "name": "object",
          "path": "$.neo_reference_id",
          "valueType": "String"
        }
      ]
    ],
    "values": [
      {
        "idx": 0,
        "path": "$.absolute_magnitude_h",
        "valueType": "Double"
      },
      {
        "idx": 1,
        "path": "$.estimated_diameter.meters.estimated_diameter_min",
        "valueType": "Double"
      },
      {
        "idx": 2,
        "path": "$.estimated_diameter.meters.estimated_diameter_max",
        "valueType": "Double"
      }
    ]
  }
]
```

<h3 id="get-dtlab-ingest-alligator-extractor-telemetry-spec-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Definition of the telemetry extraction spec.|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|404|[Not Found](https://tools.ietf.org/html/rfc7231#section-6.5.4)|Not Found|None|
||Unknown|none|None|

<h3 id="get-dtlab-ingest-alligator-extractor-telemetry-spec-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[TelemetryExtractorSpec](#schematelemetryextractorspec)]|false|none|none|
|» TelemetryExtractorSpec|[TelemetryExtractorSpec](#schematelemetryextractorspec)|false|none|none|
|»» specId|string|false|none|none|
|»» paths|[array]|false|none|none|
|»»» PathSpec|[PathSpec](#schemapathspec)|false|none|none|
|»»»» name|string|false|none|none|
|»»»» path|string|false|none|none|
|»»»» valueType|string|false|none|none|
|»» values|[[ValueSpec](#schemavaluespec)]|false|none|none|
|»»» ValueSpec|[ValueSpec](#schemavaluespec)|false|none|none|
|»»»» idx|integer|false|none|none|
|»»»» path|string|false|none|none|
|»»»» valueSpec|string|false|none|none|
|»» datetimePath|string|false|none|none|
|»» datetimeFmt|string|false|none|none|
|»» created|string|false|none|none|

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="dtlab-ingest-alligator-tell">tell</h1>

## post-dtlab-ingest-alligator-extractor-telemetry-spec

<a id="opIdpost-dtlab-ingest-alligator-extractor-telemetry-spec"></a>

> Code samples

```python
import requests
headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
}

r = requests.post('http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}', headers = headers)

print(r.json())

```

```shell
# You can also use wget
curl -X POST http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId} \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json'

```

```javascript
const inputBody = '[
  {
    "created": "2020-08-24T16:18:30.001Z",
    "datetimeFmt": "yyyy-MMM-dd hh:mm",
    "datetimePath": "$.close_approach_data[0].close_approach_date_full",
    "paths": [
      [
        {
          "name": "orbiting_body",
          "path": "$.close_approach_data[0].orbiting_body",
          "valueType": "String"
        },
        {
          "name": "object",
          "path": "$.neo_reference_id",
          "valueType": "String"
        }
      ]
    ],
    "specId": "neo1",
    "values": [
      {
        "idx": 0,
        "path": "$.absolute_magnitude_h",
        "valueType": "Double"
      },
      {
        "idx": 1,
        "path": "$.estimated_diameter.meters.estimated_diameter_min",
        "valueType": "Double"
      },
      {
        "idx": 2,
        "path": "$.estimated_diameter.meters.estimated_diameter_max",
        "valueType": "Double"
      }
    ]
  }
]';
const headers = {
  'Content-Type':'application/json',
  'Accept':'application/json'
};

fetch('http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

```java
URL obj = new URL("http://localhost:8082/dtlab-ingest-alligator/extractor/telemetry/{specId}");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`POST /dtlab-ingest-alligator/extractor/telemetry/{specId}`

*create telemetry extraction spec*

Create a new spec with that defines how to identifiy the actors to update and the telemetry to extract.

> Body parameter

```json
[
  {
    "created": "2020-08-24T16:18:30.001Z",
    "datetimeFmt": "yyyy-MMM-dd hh:mm",
    "datetimePath": "$.close_approach_data[0].close_approach_date_full",
    "paths": [
      [
        {
          "name": "orbiting_body",
          "path": "$.close_approach_data[0].orbiting_body",
          "valueType": "String"
        },
        {
          "name": "object",
          "path": "$.neo_reference_id",
          "valueType": "String"
        }
      ]
    ],
    "specId": "neo1",
    "values": [
      {
        "idx": 0,
        "path": "$.absolute_magnitude_h",
        "valueType": "Double"
      },
      {
        "idx": 1,
        "path": "$.estimated_diameter.meters.estimated_diameter_min",
        "valueType": "Double"
      },
      {
        "idx": 2,
        "path": "$.estimated_diameter.meters.estimated_diameter_max",
        "valueType": "Double"
      }
    ]
  }
]
```

<h3 id="post-dtlab-ingest-alligator-extractor-telemetry-spec-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[TelemetryExtractorSpec](#schematelemetryextractorspec)|false|The array of telemetry extraction specs associated with this specId.|
|specId|path|string|true|none|

> Example responses

> A copy of the successfully created spec definition.

```json
{
  "children": [
    "alternator_module",
    "starter_module"
  ],
  "created": "2020-07-26T18:09:06.592Z",
  "name": "machinery66",
  "props": [
    "temp",
    "speed"
  ]
}
```

> 409 Response

```json
{}
```

<h3 id="post-dtlab-ingest-alligator-extractor-telemetry-spec-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|A copy of the successfully created spec definition.|Inline|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|None|
|409|[Conflict](https://tools.ietf.org/html/rfc7231#section-6.5.8)|Conflict - you must delete the previous entry before creating a type of the same name.|Inline|

<h3 id="post-dtlab-ingest-alligator-extractor-telemetry-spec-responseschema">Response Schema</h3>

Status Code **201**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[TelemetryExtractorSpec](#schematelemetryextractorspec)]|false|none|none|
|» TelemetryExtractorSpec|[TelemetryExtractorSpec](#schematelemetryextractorspec)|false|none|none|
|»» specId|string|false|none|none|
|»» paths|[array]|false|none|none|
|»»» PathSpec|[PathSpec](#schemapathspec)|false|none|none|
|»»»» name|string|false|none|none|
|»»»» path|string|false|none|none|
|»»»» valueType|string|false|none|none|
|»» values|[[ValueSpec](#schemavaluespec)]|false|none|none|
|»»» ValueSpec|[ValueSpec](#schemavaluespec)|false|none|none|
|»»»» idx|integer|false|none|none|
|»»»» path|string|false|none|none|
|»»»» valueSpec|string|false|none|none|
|»» datetimePath|string|false|none|none|
|»» datetimeFmt|string|false|none|none|
|»» created|string|false|none|none|

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_TelemetryExtractorSpec">TelemetryExtractorSpec</h2>
<!-- backwards compatibility -->
<a id="schematelemetryextractorspec"></a>
<a id="schema_TelemetryExtractorSpec"></a>
<a id="tocStelemetryextractorspec"></a>
<a id="tocstelemetryextractorspec"></a>

```json
{
  "specId": "string",
  "paths": [
    [
      {
        "name": "string",
        "path": "string",
        "valueType": "string"
      }
    ]
  ],
  "values": [
    {
      "idx": 0,
      "path": "string",
      "valueSpec": "string"
    }
  ],
  "datetimePath": "string",
  "datetimeFmt": "string",
  "created": "string"
}

```

TelemetryExtractorSpec

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|specId|string|false|none|none|
|paths|[array]|false|none|none|
|values|[[ValueSpec](#schemavaluespec)]|false|none|none|
|datetimePath|string|false|none|none|
|datetimeFmt|string|false|none|none|
|created|string|false|none|none|

<h2 id="tocS_PathSpec">PathSpec</h2>
<!-- backwards compatibility -->
<a id="schemapathspec"></a>
<a id="schema_PathSpec"></a>
<a id="tocSpathspec"></a>
<a id="tocspathspec"></a>

```json
{
  "name": "string",
  "path": "string",
  "valueType": "string"
}

```

PathSpec

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|false|none|none|
|path|string|false|none|none|
|valueType|string|false|none|none|

<h2 id="tocS_ValueSpec">ValueSpec</h2>
<!-- backwards compatibility -->
<a id="schemavaluespec"></a>
<a id="schema_ValueSpec"></a>
<a id="tocSvaluespec"></a>
<a id="tocsvaluespec"></a>

```json
{
  "idx": 0,
  "path": "string",
  "valueSpec": "string"
}

```

ValueSpec

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|idx|integer|false|none|none|
|path|string|false|none|none|
|valueSpec|string|false|none|none|

<h2 id="tocS_ObjectExtractorSpec">ObjectExtractorSpec</h2>
<!-- backwards compatibility -->
<a id="schemaobjectextractorspec"></a>
<a id="schema_ObjectExtractorSpec"></a>
<a id="tocSobjectextractorspec"></a>
<a id="tocsobjectextractorspec"></a>

```json
{
  "specId": "string",
  "path": "string",
  "telSpecId": "string",
  "created": "string"
}

```

ObjectExtractorSpec

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|specId|string|false|none|none|
|path|string|false|none|none|
|telSpecId|string|false|none|none|
|created|string|false|none|none|

<h2 id="tocS_Telemetry">Telemetry</h2>
<!-- backwards compatibility -->
<a id="schematelemetry"></a>
<a id="schema_Telemetry"></a>
<a id="tocStelemetry"></a>
<a id="tocstelemetry"></a>

```json
{
  "idx": "string",
  "value": 0,
  "datetime": "string"
}

```

Telemetry

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|idx|string|false|none|none|
|value|number|false|none|none|
|datetime|string|false|none|none|

