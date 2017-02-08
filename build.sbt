def twitterUtil(mod: String) =
  "com.twitter" %% s"util-$mod" %  "6.40.0"

def finagle(mod: String) =
  "com.twitter" %% s"finagle-$mod" % "6.41.0"

def telemetery(mod: String) =
  "io.buoyant" %% s"telemetry-$mod" % "0.8.6-SNAPSHOT" // TODO: switch to stable

def zipkin(mod: String) =
  "io.zipkin.finagle" %% s"zipkin-finagle-$mod" % "0.3.4"

def scalatest() =
  "org.scalatest" %% "scalatest" % "2.2.4"

val `linkerd-zipkin` =
  project.in(file("."))
    .enablePlugins(DockerPlugin)
    .settings(
      organization := "io.buoyant",
      version := "0.0.1",
      scalaVersion in GlobalScope := "2.11.7",
      ivyScala := ivyScala.value.map(_.copy(overrideScalaVersion = true)),
      resolvers ++= Seq(
        "twitter-repo" at "https://maven.twttr.com",
        Resolver.mavenLocal,
        "typesafe" at "https://repo.typesafe.com/typesafe/releases"
      ),
      libraryDependencies ++= Seq(
        finagle("core"),
        finagle("zipkin-core"),
        telemetery("core"),
        twitterUtil("stats"),
        zipkin("http"),
        zipkin("kafka"),
        scalatest() % "test"
      ),
      aggregate in assembly := false,
      assemblyMergeStrategy in assembly := {
        case "BUILD" => MergeStrategy.discard
        case "com/twitter/common/args/apt/cmdline.arg.info.txt.1" => MergeStrategy.discard
        case "META-INF/io.netty.versions.properties" => MergeStrategy.last
        case path => (assemblyMergeStrategy in assembly).value(path)
      },
      assemblyJarName in assembly := s"${name.value}-${version.value}.jar",
      docker <<= (docker dependsOn assembly),
      dockerfile in docker := new Dockerfile {
        from("buoyantio/linkerd:0.8.6-SNAPSHOT") // TODO: switch to stable
        val pluginSrc = (assemblyOutputPath in assembly).value
        val pluginTarget = "$L5D_HOME/plugins/" + (assemblyJarName in assembly).value
        copy(pluginSrc, pluginTarget)
      },
      imageNames in docker := Seq(ImageName(s"linkerd/${name.value}"))
    )
