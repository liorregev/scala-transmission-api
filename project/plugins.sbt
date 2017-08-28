logLevel := Level.Warn

resolvers += Resolver.bintrayRepo("sbt", "sbt-plugin-releases")

// For sbt-level logging
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.4" exclude("org.apache.maven", "maven-plugin-api"))
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0") // Run with `sbt dependencyUpdates`
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.1.1") // https://github.com/puffnfresh/wartremover/issues/294
addSbtPlugin("org.wartremover" % "sbt-wartremover-contrib" % "1.0.0")
addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt" % "0.5.3")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC2")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")