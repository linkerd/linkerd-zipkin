// packaging
addSbtPlugin("com.eed3si9n"      % "sbt-assembly"    % "0.14.1")

// pgp signing for publishing to sonatype
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
// automate sonatype publishing
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")
