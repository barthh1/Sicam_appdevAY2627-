# Sicam_appdevAY2627-
LabActivity0

Laboratory Discussion: Discuss the primary challenges you encountered while setting up your native Android Java development environment and executing your initial project build. Consider specific hurdles related to installing Android Studio, configuring JDK and platform path variables (JAVA_HOME, ANDROID_HOME), resolving Gradle sync failures, downloading missing Android SDK components or build tools, and accepting SDK licenses. Additionally, examine challenges faced when launching Android Virtual Devices (AVDs) or configuring physical Android devices with USB Debugging enabled.


Explain the troubleshooting steps, Gradle build logs, and documentation resources you utilized to diagnose and overcome these roadblocks. Detail how resolving these issues improved your understanding of the native Android build system, the Gradle lifecycle, dependency management, agind Android SDK architecture.


LabActivity1

During screen rotation, the Activity is usually destroyed and recreated, so lifecycle methods like onPause(), onStop(), onDestroy(), onCreate(), and onStart() are called. When pressing the Home button, the Activity goes to the background, triggering onPause() and onStop(), but it is not necessarily destroyed. Logcat helped me check the sequence of lifecycle methods and understand what happens during each action. I also experienced some setup issues, such as Gradle sync delays and configuration errors, but I was able to fix them and run the application successfully.