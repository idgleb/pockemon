# Здесь можно добавить правила ProGuard, специфичные для проекта.
# Набор конфигурационных файлов управляется параметром
# proguardFiles в build.gradle.
#
# Подробнее:
#   http://developer.android.com/guide/developing/tools/proguard.html

# Если проект использует WebView с JS, раскомментируйте и укажите
# полное имя класса JavaScript-интерфейса:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Сохранить номера строк для отладки стек-трейсов:
#-keepattributes SourceFile,LineNumberTable

# Если сохраняете номера строк, можно скрыть имя исходного файла:
#-renamesourcefileattribute SourceFile