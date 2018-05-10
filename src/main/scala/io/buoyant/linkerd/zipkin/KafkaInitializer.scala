package io.buoyant.linkerd.zipkin

import com.twitter.finagle.{Stack, param}
import com.twitter.finagle.stats.NullStatsReceiver
import com.twitter.finagle.tracing.Tracer
import com.twitter.finagle.zipkin.core.Sampler
import io.buoyant.telemetry.{Telemeter, TelemeterConfig, TelemeterInitializer}
import zipkin.finagle.kafka.KafkaZipkinTracer

class KafkaInitializer extends TelemeterInitializer {
  type Config = KafkaConfig
  val configClass = classOf[KafkaConfig]
  override val configId = "io.zipkin.kafka"
}

case class KafkaConfig(
  bootstrapServers: Option[String],
  topic: Option[String],
  initialSampleRate: Option[Double]
) extends TelemeterConfig {

  private[this] val config: KafkaZipkinTracer.Config = KafkaZipkinTracer.Config.builder()
    .bootstrapServers(bootstrapServers.getOrElse("localhost:9092"))
    .topic(topic.getOrElse("kafka"))
    .initialSampleRate(initialSampleRate.map(_.toFloat).getOrElse(Sampler.DefaultSampleRate))
    .build()

  def mk(params: Stack.Params): KafkaTelemeter = {
    val param.Stats(stats) = params[param.Stats]
    val tracer = KafkaZipkinTracer.create(config, stats.scope("io.zipkin.kafka"))
    new KafkaTelemeter(tracer)
  }
}

class KafkaTelemeter(underlying: Tracer) extends Telemeter {
  val stats = NullStatsReceiver
  lazy val tracer = underlying
  def run() = Telemeter.nopRun
}
