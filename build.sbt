
val linkerdVersion = "1.3.4"

def twitterUtil(mod: String) =
  "com.twitter" %% s"util-$mod" %  "7.1.0"

def finagle(mod: String) =
  "com.twitter" %% s"finagle-$mod" % "7.1.0"

def telemetry(mod: String) =
  "io.buoyant" %% s"telemetry-$mod" % linkerdVersion

def zipkin(mod: String) =
  "io.zipkin.finagle" %% s"zipkin-finagle-$mod" % "1.1.0"

def scalatest() =
  "org.scalatest" %% "scalatest" % "3.0.1"

val `linkerd-zipkin` =
  project.in(file("."))
    .settings(
      organization := "io.buoyant",
      version := linkerdVersion,
      homepage := Some(url("https://linkerd.io")),
      scalaVersion in GlobalScope := "2.12.1",
      ivyScala := ivyScala.value.map(_.copy(overrideScalaVersion = true)),
      resolvers ++= Seq(
        "twitter-repo" at "https://maven.twttr.com",
        Resolver.mavenLocal,
        "typesafe" at "https://repo.typesafe.com/typesafe/releases"
      ),
      libraryDependencies ++= Seq(
        finagle("core"),
        finagle("zipkin-core"),
        telemetry("core"),
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
      assemblyOutputPath in assembly := file(s"plugins/${(assemblyJarName in assembly).value}"),
      // Sonatype publishinga
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      publishMavenStyle := true,
      pomExtra :=
        <licenses>
          <license>
            <name>Apache License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:linkerd/linkerd-zipkin.git</url>
          <connection>scm:git:git@github.com:linkerd/linkerd-zipkin.git</connection>
        </scm>
        <developers>
          <developer>
            <id>buoyant</id>
            <name>Buoyant Inc.</name>
            <url>https://buoyant.io/</url>
          </developer>
        </developers>,
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (version.value.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      }
    )
