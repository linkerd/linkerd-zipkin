package io.buoyant.linkerd.zipkin

import com.fasterxml.jackson.databind.JsonMappingException
import com.twitter.finagle.Stack
import com.twitter.finagle.util.LoadService
import io.buoyant.config.Parser
import io.buoyant.telemetry.{TelemeterConfig, TelemeterInitializer}
import org.scalatest._

class KafkaInitializerTest extends FunSuite {

  test("valid config") {
    val yaml =
      """|kind: io.zipkin.kafka
         |bootstrapServers: 1.2.3.4:9411
         |topic: foo
         |initialSampleRate: 0.5
         |""".stripMargin

    val mapper = Parser.objectMapper(yaml, Seq(LoadService[TelemeterInitializer]))
    val config = mapper.readValue[TelemeterConfig](yaml)
    assert(config.mk(Stack.Params.empty).isInstanceOf[KafkaTelemeter])
  }

  test("invalid config") {
    val yaml =
      """|kind: io.zipkin.kafka
         |noSuchAttr: foo
         |""".stripMargin

    val mapper = Parser.objectMapper(yaml, Seq(LoadService[TelemeterInitializer]))
    intercept[JsonMappingException] { val _ = mapper.readValue[TelemeterConfig](yaml) }
  }

}
