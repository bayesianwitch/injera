import sbt._
import Defaults._
import Keys._

object ApplicationBuild extends Build {

  lazy val commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.bayesianwitch",
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    scalaVersion := "2.11.0",
    crossScalaVersions  := Seq("2.11.0", "2.10.3"),
    version := "0.01.6",
    resolvers ++= myResolvers,
    name := "injera",
    //fork := true,
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % "7.0.6",
      "com.google.code.findbugs" % "jsr305" % "2.0.2", //necessary for guava to work
      "com.google.guava" % "guava" % "15.0",
      "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
    ),
    publishTo := Some(Resolver.file("file",  new File( "/tmp/injera-publish" )) )
  )

  val myResolvers = Seq("Sonatatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
			"Sonatatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
			"Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
			"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots",
			"Coda Hale" at "http://repo.codahale.com"
		      )

  lazy val injera = Project("injera", file("."), settings = commonSettings)
}
