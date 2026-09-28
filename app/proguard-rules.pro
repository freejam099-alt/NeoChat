# DeepWiki Proguard Rules
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
-keep class com.deepwiki.app.model.** { *; }
