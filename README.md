
# Displayer

An open source multi-platform app for digital signage

## Introduction

The _Displayer_ app shows your content, described in a JSON file, in an endless loop, without any intervention, on the device of your choice.

Some of its features are:
 * Works on multiple platforms:
   * Android, including Android TV
   * Linux
   * Any other platform supported by Kotlin Multiplatform (KMP) 
 * Multiple _regions_
   * _Center_ region to show your main content
   * _Left_ and _Bottom_ region to show secondary (scrolling) content 
 * Different types of content:
   * Text
   * Images
   * Clock
   * Live weather information (requires an OpenWeather API key)
   * Random content
 * Locale-aware (for time formatting, temperature units,...)
 * Is a _launcher_ on Android platforms (including Android TV)

Displayer is being used successfully in the club house of [korfbalclub KCBJ](https://www.kcbj.be/) to display information about upcoming events, sponsors, live weather etc, without any intervention of the volunteer currently doing bar service.

## Build and run

There are currently no binary builds available. You have to download/clone the source code and build the app to run it.

Building Displayer requires at minimum Java 17.

Optionally, you can open the project in IntelliJ IDEA (tested with version 2022.3.1 of the Community Edition running on Java 17)

### Android

 * Define `sdk.dir` in `local.properties` to point to your Android SDK location 
 * Execute `./gradlew assemble` to build the Android app
 * The resulting APK files will be placed in `android/build/outputs/apk`
 * Use [ADB](https://developer.android.com/studio/command-line/adb) to install the app on your Android device: `adb install android/build/outputs/apk/debug/android-debug.apk`

### Desktop

 * Execute `./gradlew package` to build the desktop apps
 * The resulting binaries will be placed in `desktop/build/compose/binaries`

## Display file structure

Coming soon...

There are sample display files available in the `samples` directory.

## Send commands

### Android

On Android, you'll need [ADB](https://developer.android.com/studio/command-line/adb) to send commands to Displayer.
Commands are sent to the app as Android _intents_ with _extras_.

#### Load a display file

```
adb shell am start -a "android.intent.action.VIEW" -d "URL_OF_DISPLAY_FILE"
```

Replace URL_OF_DISPLAY_FILE with the URL of your own display file, e.g. https://example.com/displayer.json

#### Set OpenWeather API key

```
adb shell am start -a "com.displayer.action.CONFIG" -e EXTRA_OPEN_WEATHER_API_KEY YOUR_API_KEY
```

Replace YOUR_API_KEY with your own OpenWeather API key obtained from the [OpenWeather API console](https://home.openweathermap.org/api_keys).

### Desktop

On desktop, you must start Displayer with extra parameters to enable the admin endpoint.
This endpoint is needed to send commands to the app (using `curl`, `wget` or any web browser).

Example:
```
/opt/displayer/bin/Displayer --port=YOUR_PORT --secret=YOUR_SECRET
```

Replace YOUR_PORT with the TCP/IP port number to use for the admin endpoint.

Replace YOUR_PORT with a secret that must be included in any request to the admin port.

#### Load a display file

```
curl "http://YOUR_HOST:YOUR_PORT/config?secret=YOUR_SECRET&url=URL_OF_DISPLAY_FILE"
```

Replace URL_OF_DISPLAY_FILE with the URL of your own display file, e.g. https://example.com/displayer.json.
Make sure you URL-encode all special characters in the URL.

#### Set OpenWeather API key

```
curl "http://YOUR_HOST:YOUR_PORT/config?secret=YOUR_SECRET&open-weather-api-key=YOUR_API_KEY"
```

Replace YOUR_API_KEY with your own OpenWeather API key obtained from the [OpenWeather API console](https://home.openweathermap.org/api_keys).

## Android TV

After installing Displayer on Android TV, you can disable the original Android TV launcher.
This will make sure that Displayer automatically starts after rebooting your Android TV device.

To disable the original Android TV launcher enter the following commands:

```
adb shell pm disable-user --user 0 com.google.android.tvlauncher
adb shell pm disable-user --user 0 com.google.android.tungsten.setupwraith
```

You can always re-enable the original Android TV launcher by entering the following commands:

```
adb shell pm enable com.google.android.tvlauncher
adb shell pm enable com.google.android.tungsten.setupwraith
```
