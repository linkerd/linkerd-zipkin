# linkerd-zipkin

This repo contains linkerd telemeters for writing tracing data to Zipkin, using
linkerd's plugin interface and Zipkin's [finagle-zipkin](
https://github.com/openzipkin/zipkin-finagle) library.

## Building

### Plugin jar

To build a plugin jar, run:

```bash
./sbt assembly
```

That will build a linkerd-zipkin plugin jar in `target/scala-2.11/`. Put that
jar in linkerd's class path to make the telemeters available.

### Docker

If you're using docker, you can instead use a docker image that already has
telemeters loaded. To build the docker image, run:

```bash
./sbt assembly
```

That will build a docker image called `linkerd/linkerd-zipkin`, which runs
linkerd and autoloads the telemeters from this repo.

## Usage

Two telemeters are provided, for writing to Zipkin with different transports.

### io.zipkin.http

This telemeter writes tracing data to zipkin over HTTP. Sample configuration:

```yaml
telemetry:
- kind: io.zipkin.http
  host: localhost:9411
  initialSampleRate: 0.02
```

Available configuration options for this telemeter:

Option | Default | Description
--- | --- | ---
`host` | localhost:9411 | The network location of the Zipkin http service.
`hostHeader` | zipkin | The Host header used when sending spans to Zipkin
`compressionEnabled` | true | True implies that spans will be gzipped before transport
`initialSampleRate` | 0.001 (0.1%) | Percentage of traces to sample in the range [0.0 - 1.0]

### io.zipkin.kafka

This telemeter writes tracing data to zipkin using Kafka. Sample configuration:

```yaml
telemetry:
- kind: io.zipkin.kafka
  bootstrapServers: localhost:9092
  initialSampleRate: 0.02
```

Available configuration options for this telemeter:

Option | Default | Description
--- | --- | ---
`bootstrapServers` | localhost:9092 | Initial set of kafka servers to connect to (comma separated)
`topic` | zipkin | Kafka topic zipkin traces will be sent to
`initialSampleRate` | 0.001 (0.1%) | Percentage of traces to sample in the range [0.0 - 1.0]
