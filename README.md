# HandyASR

Короткое описание
------------------
HandyASR — Android-приложение на Kotlin с использованием Jetpack Compose. Предназначено для демонстрации работы с распознаванием речи и хранением результатов (локальная БД), сетевого взаимодействия и современного стека Android.

Ключевые возможности
--------------------
- Интерфейс на Jetpack Compose (Material3)
- Локальное хранилище записей (Room)
- Сетевые вызовы через Retrofit + Kotlinx Serialization
- Навигация через Navigation Compose

Быстрый старт
---------------------
1. Клонируйте репозиторий:

```bash
git clone git@github.com:MelancholicBastard/HandyASR.git
cd HandyASR
```

2. Создайте файл `local.properties` и укажите путь к Android SDK (если Android Studio не сделает этого автоматически) и задайте переменную `BASE_URL` (см. ниже). В проекте `BASE_URL` читается в `app/build.gradle.kts` и попадает в `BuildConfig.BASE_URL`, поэтому нужно указывать ссылку на веб-сокет для расшифровки аудио в `local.properties` или передавать в `./gradlew` через флаг `-P`.


3. Соберите и установите debug-сборку на подключённое устройство / эмулятор:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

Требования
----------
- JDK: Java 11 (проект настроен на Java 11)
- Android SDK: compileSdk = 36 (minor API 36.1), targetSdk = 36, minSdk = 24
- Android Gradle Plugin (AGP): 9.1.0
- Kotlin: 2.2.10

Основные зависимости
-------------------------------------------------------------------
- AndroidX Compose (Compose BOM: 2026.03.00)
- material3 (Jetpack Compose Material3)
- navigation-compose
- androidx.core:core-ktx
- appcompat
- com.google.android.material:material 1.13.0
- lifecycle-viewmodel-ktx
- kotlinx-serialization-json 1.11.0
- Retrofit 3.0.0 + retrofit2-kotlinx-serialization-converter
- OkHttp 5.3.2
- Room 2.8.4 (ksp: 2.2.10-2.0.2)
- androidx.benchmark:benchmark-common

Сборка и запуск
--------------------------------
- Сделать gradle wrapper исполняемым (один раз):

```bash
chmod +x ./gradlew
```

- Собрать debug APK/AAB:

```bash
./gradlew assembleDebug
```

- Собрать и установить debug APK на подключённое устройство / эмулятор:

```bash
./gradlew installDebug
```

- Собрать релизную сборку (подпись не настроена автоматически):

```bash
./gradlew assembleRelease
```

- Очистить сборку:

```bash
./gradlew clean
```

Запуск в Android Studio
-----------------------
1. Откройте Android Studio → Open → выберите корневую папку `HandyASR`.
2. Дождитесь синхронизации Gradle. Если Android Studio предложит обновить плагины — следуйте подсказкам, но лучше использовать версии, указанные в `gradle/libs.versions.toml`.
3. Выберите конфигурацию `app` и нажмите Run/Debug.

Отладка и логирование
---------------------
- Logcat: используйте Logcat в Android Studio или команду `adb logcat`.
- Установка breakpoint'ов и запуск в режиме Debug через Android Studio.

Частые проблемы и решения
-------------------------

- Ошибка: права на `gradlew` — выполните `chmod +x ./gradlew`.
- Ошибка: missing `local.properties` или пустой `BASE_URL` — создайте `local.properties` и добавьте `BASE_URL` при необходимости.
- Ошибки KSP/Room (annotation processing) — убедитесь, что версия `ksp` соответствует версии Kotlin в `gradle/libs.versions.toml` и что плагин KSP подключён (см. `app/build.gradle.kts`).
- Установка APK не проходит — проверьте `adb devices` и включите отладку по USB на устройстве.
- Конфликты зависимостей — попробуйте `./gradlew dependencies --configuration debugCompileClasspath` и `./gradlew --refresh-dependencies`.
