# Welcome to Tier's Android Coding Challenge

The initial goal of this project is to create a fully functional application, in a simple manner, without the need of over engineering, organized in a way that the code would be easily perceived by the majority of Android developers originated from different levels of experience.

Tier app asks the user's location permission in order to indicate his/hers current position on the map while loading vehicle data in the background from a REST API and presents them as markers. Tier app clusters those markers so that the user is able to observe the total of vehicles in a specific area. By tapping on a cluster the map zooms in, demostrating the markers in this area, To locate and get further information about the vehicle that is closest to him/her, the user has to tap on the FAB button. Those valuables details like the vehicle's distance, the battery level etc, are displayed on a bottom sheet. Apart from his/her access to the details of the closest vehicle, he/she can also be informed for the details of any other vehicle by tapping of a marker.

![Screenshot_map](https://github.com/domgeorg/Tier/blob/master/screenshots/screenshot_map.png)

![Screenshot_details](https://github.com/domgeorg/Tier/blob/master/screenshots/screenshot_details.png)

## Technologies and patterns

Dependency injection with Hilt

Jetpack Navigation Component

View Binding

Coroutines

Retrofit + OkHttp + Moshi

MVVM + LiveData

Firebase-crashlytics

Mockito for the unit tests

Ktlint

## Credits

[Gabor Varadi](https://github.com/Zhuinden) for ```FragmentViewBindingDelegate```
[Simple one-liner ViewBinding in Fragments and Activities with Kotlin](https://zhuinden.medium.com/simple-one-liner-viewbinding-in-fragments-and-activities-with-kotlin-961430c6c07c)

## Ktlint for static code analysis

 Tier app will follow the official code style from [kotlinlang.org](https://kotlinlang.org/docs/coding-conventions.html) and [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide) with the assistance of [ktlint](https://ktlint.github.io/) an open-source lining tool for Kotlin which also includes a build-in code formatter.
 
 To check the code’s formatting, run the following command from the command line
 ```
./gradlew ktlintCheck
```
This will run through the project and report back any errors which are found using the default ktlint-gradle plugin configuration.

To automatically fix any errors which are reported by ktlintCheck, you can run the following command from the command line:
 ```
./gradlew ktlintFormat
```
This will attempt to auto-fix any errors and will report back any issues that could not automatically be fixed.

## Pre-commit Git-hook

This hook automates ktlint static code analysis before a commited change-list.

## Pre-push Git-hook

This hook uses  ```./gradlew clean test``` to run the tests.

Having this basic setup can improve any team’s workflow and code cleanliness while ensuring that any pushed commit builds and doens't break the tests.These commands run usually on Jenkins using Fastlane.

## Version control

I prefer the standard [Git Flow methodology](https://nvie.com/posts/a-successful-git-branching-model/)

## Thanks for taking the time to review my code
For any questions you can find me [here](https://www.linkedin.com/in/kyriakos-georgiopoulos/)
