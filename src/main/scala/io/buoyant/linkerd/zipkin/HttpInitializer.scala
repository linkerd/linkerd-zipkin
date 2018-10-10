package io.buoyant.linkerd.zipkin

import com.twitter.finagle.Stack
import com.twitter.finagle.stats.NullStatsReceiver
import com.twitter.finagle.tracing.Tracer
import com.twitter.finagle.zipkin.core.Sampler
import io.buoyant.telemetry.{Telemeter, TelemeterConfig, TelemeterInitializer}
import zipkin2.finagle.http.HttpZipkinTracer

class HttpInitializer extends TelemeterInitializer {
  type Config = HttpConfig
  val configClass = classOf[HttpConfig]
  override val configId = "io.zipkin.http"
}

case class HttpConfig(
  host: Option[String],
  hostHeader: Option[String],
  compressionEnabled: Option[Boolean],
  initialSampleRate: Option[Double]
) extends TelemeterConfig {

  private[this] val tracer: Tracer = {
    val config = HttpZipkinTracer.Config.builder()
      .host(host.getOrElse("localhost:9411"))
      .hostHeader(hostHeader.getOrElse("zipkin"))
      .compressionEnabled(compressionEnabled.getOrElse(true))
      .initialSampleRate(initialSampleRate.map(_.toFloat).getOrElse(Sampler.DefaultSampleRate))
      .build()

    HttpZipkinTracer.create(config, NullStatsReceiver)
  }

  def mk(params: Stack.Params): HttpTelemeter = new HttpTelemeter(tracer)
}

class HttpTelemeter(underlying: Tracer) extends Telemeter {
  val stats = NullStatsReceiver
  lazy val tracer = underlying
  def run() = Telemeter.nopRun
}
