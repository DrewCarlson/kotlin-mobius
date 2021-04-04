# Mobius.kt

![Maven Central](https://img.shields.io/maven-central/v/org.drewcarlson/mobiuskt-core-jvm?label=maven&color=blue)
![](https://github.com/DrewCarlson/mobius.kt/workflows/Jvm/badge.svg)
![](https://github.com/DrewCarlson/mobius.kt/workflows/Js/badge.svg)
![](https://github.com/DrewCarlson/mobius.kt/workflows/Native/badge.svg)

Kotlin Multiplatform [Mobius](https://github.com/spotify/mobius) implementation.

## What is Mobius?

The core construct provided by Mobius is the Mobius Loop, best described by the official documentation. _(Embedded below)_

A Mobius loop is a part of an application, usually including a user interface.
In a Spotify context, there is usually one loop per feature such as “the album page”, “login flow”, etc., but a loop can also be UI-less and for instance be tied to the lifecycle of an application or a user session.

### Mobius Loop

![Mobius Loop Diagram](https://raw.githubusercontent.com/wiki/spotify/mobius/mobius-diagram.png)

> A Mobius loop receives [Events](https://github.com/spotify/mobius/wiki/Event), which are passed to an [Update](https://github.com/spotify/mobius/wiki/Update) function together with the current [Model](https://github.com/spotify/mobius/wiki/Model).
> As a result of running the Update function, the Model might change, and [Effects](https://github.com/spotify/mobius/wiki/Effect) might get dispatched.
> The Model can be observed by the user interface, and the Effects are received and executed by an [Effect Handler](https://github.com/spotify/mobius/wiki/Effect-Handler).

'Pure' in the diagram refers to pure functions, functions whose output only depends on their inputs, and whose execution has no observable side effects.
 See [Pure vs Impure Functions](https://github.com/spotify/mobius/wiki/Pure-vs-Impure-Functions) for more details.

_(Source: [Concepts > Mobius Loop](https://github.com/spotify/mobius/wiki/Concepts/53777574e070e168f2c3bdebc1be544edfcee2cf#mobius-loop))_

By combining this concept with Kotlin's MPP features, mobius.kt allows you to write and test all of your pure functions (application and/or business logic) in Kotlin and deploy it everywhere.
This leaves impure functions to the native platform, which can be written in their primary language (Js, Java, Objective-c/Swift) or in Kotlin!

## Download

![Maven Central](https://img.shields.io/maven-central/v/org.drewcarlson/mobiuskt-core-jvm?label=maven&color=blue)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/org.drewcarlson/mobiuskt-core-jvm?server=https%3A%2F%2Fs01.oss.sonatype.org)

![](https://img.shields.io/static/v1?label=&message=Platforms&color=grey)
![](https://img.shields.io/static/v1?label=&message=Js&color=blue)
![](https://img.shields.io/static/v1?label=&message=Jvm&color=blue)
![](https://img.shields.io/static/v1?label=&message=Linux&color=blue)
![](https://img.shields.io/static/v1?label=&message=macOS&color=blue)
![](https://img.shields.io/static/v1?label=&message=Windows&color=blue)
![](https://img.shields.io/static/v1?label=&message=iOS&color=blue)
![](https://img.shields.io/static/v1?label=&message=tvOS&color=blue)
![](https://img.shields.io/static/v1?label=&message=watchOS&color=red)

```kotlin
repositories {
  mavenCentral()
  // Or snapshots
  maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}
dependencies {
  implementation("org.drewcarlson:mobiuskt-core:$MOBIUS_VERSION")
  implementation("org.drewcarlson:mobiuskt-extras:$MOBIUS_VERSION")
  implementation("org.drewcarlson:mobiuskt-android:$MOBIUS_VERSION")
}
```
