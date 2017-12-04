def twitterUtil(mod: String) =
  "com.twitter" %% s"util-$mod" %  "7.1.0"

def finagle(mod: String) =
  "com.twitter" %% s"finagle-$mod" % "7.1.0"

def telemetery(mod: String) =
  "io.buoyant" %% s"telemetry-$mod" % "1.3.0"

def zipkin(mod: String) =
  "io.zipkin.finagle" %% s"zipkin-finagle-$mod" % "1.1.0"

def scalatest() =
  "org.scalatest" %% "scalatest" % "3.0.1"

val `linkerd-zipkin` =
  project.in(file("."))
    .settings(
      organization := "io.buoyant",
      version := "0.0.1",
      scalaVersion in GlobalScope := "2.12.1",
      scalaModuleInfo := scalaModuleInfo
        .value
        .map(_.withOverrideScalaVersion(true)),
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
      assemblyOutputPath in assembly := file(s"plugins/${(assemblyJarName in assembly).value}")
    )
