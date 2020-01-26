# wiremock-metrics

Extension to add telemetry support to [Wiremock](https://github.com/tomakehurst/wiremock).

## Metrics
| metric name            | description                                           | tags                                                                                                      |
|------------------------|-------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| requestTimer_ms_count  | count of requests                                     | path - url stub, pattern status - http status, method - http method                                       |
| requestTimer_ms_sum    | summary of latency in milliseconds                    | path - url stub, pattern status - http status, method - http method                                       |
| requestTimer_ms_bucket | Latency buckets, to be used with percentile functions | le - less equal, path - url stub pattern, status - http status, method - http method |
| wiremock_metrics_errors_total| Errors while calculating metrics                      | errType - error type | 
## Build
./gradlew clean fatJar
Ready to use jar will be under build/libs

## Use
after creating the jar above, you can run wiremock with prometheus telemetry.

### Code
`WireMockServer server = new WireMockServer(conf.port(8092).extensions(new PrometheusExporterExtension(), new MetricsExtension()));`

### Standalone (CLI)
add the generated jar to some dir (/WIREMOCK-EXTENSIONS-PATH/lib/)
`java -cp /WIREMOCK-PATH/lib/*:/WIREMOCK-EXTENSIONS-PATH/lib/* com.github.tomakehurst.wiremock.standalone.WireMockServerRunner --extensions=wiremock.PrometheusExporterExtension,wiremock.MetricsExtension`

prometheus endpoint will be exposed as `/__admin/metrics`