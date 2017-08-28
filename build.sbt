// Every once in a while run `sbt dependencyUpdates` here and in project/
import Tests._

enablePlugins(GitVersioning)
git.useGitDescribe := true

name := "scala-transmission-api"
scalaVersion := "2.12.3"
organization := "com.liorregev"

lazy val defaultSettings = Seq(

  javaOptions ++= Seq("-Xms512M", "-Xmx8192M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled"),

  scalacOptions ++= Seq(
    "-feature", "-deprecation", "-unchecked", "-explaintypes",
    "-encoding", "UTF-8", // yes, this is 2 args
    "-language:reflectiveCalls", "-language:implicitConversions", "-language:postfixOps", "-language:existentials",
    "-language:higherKinds",
    // http://blog.threatstack.com/useful-scala-compiler-options-part-3-linting
    "-Xcheckinit", "-Xexperimental", "-Xfatal-warnings", /*"-Xlog-implicits", */"-Xfuture", "-Xlint",
    "-Ywarn-dead-code", "-Ywarn-inaccessible", "-Ywarn-numeric-widen", "-Yno-adapted-args", "-Ywarn-unused-import",
    "-Ywarn-unused"
  ),

  wartremoverErrors ++= Seq(
    Wart.StringPlusAny, Wart.FinalCaseClass, Wart.JavaConversions, Wart.Null, Wart.Product, Wart.Serializable,
    Wart.LeakingSealed, Wart.While, Wart.Return, Wart.ExplicitImplicitTypes, Wart.Enumeration, Wart.FinalVal,
    Wart.TryPartial, Wart.TraversableOps, Wart.OptionPartial, Wart.ArrayEquals, ContribWart.SomeApply/*, TODO:
    ContribWart.ExposedTuples, ContribWart.OldTime */
  ),

  wartremoverWarnings ++= wartremover.Warts.allBut(
    Wart.Nothing, Wart.DefaultArguments, Wart.Throw, Wart.MutableDataStructures, Wart.NonUnitStatements, Wart.Overloading,
    Wart.Option2Iterable, Wart.ImplicitConversion, Wart.ImplicitParameter, Wart.Recursion,
    Wart.Any, Wart.Equals, // Too many warnings because of spark's Row
    Wart.AsInstanceOf // Too many warnings because of spark's UDF
  ),

  testFrameworks := Seq(TestFrameworks.ScalaTest),
  logBuffered in Test := false,

  resolvers ++= Seq(
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("public"),
    Resolver.typesafeRepo("releases"),
    Resolver.bintrayRepo("dwhjames", "maven")
  ),

  // This needs to be here for Coursier to be able to resolve the "tests" classifier, otherwise the classifier's ignored
  classpathTypes += "test-jar",

  libraryDependencies ++= Seq(
    // The following dependencies are provided by EMR. When upgrading an EMR version, upgrade them too.
    "com.typesafe.play" %% "play-ws-standalone"   % "1.0.4",
    "com.typesafe.play" %% "play-json"            % "2.6.3",
    "org.scalatest"     %% "scalatest"            % "3.0.1"       % "test"
  )
)

lazy val assemblySettings = Seq(
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true),
  test in assembly := {}
)


lazy val root = project.in(file("."))
  .settings(defaultSettings ++ assemblySettings)
  .settings(
    // Allow parallel execution of tests as long as each of them gets its own JVM to create a SparkContext on (see SPARK-2243)
    fork in Test := true,
    testGrouping in Test := (definedTests in Test)
      .value
      .map(test => Group(test.name, Seq(test), SubProcess(ForkOptions())))
  )