# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Call Blocker: a small Android app (Kotlin, single `app` module, package `com.tk.callblocker`, minSdk 29 / target 34) that rejects incoming calls from numbers not in the user's contacts. There are no tests and no lint config beyond Android defaults; CI is only a CodeQL workflow (`.github/workflows/codeql.yml`).

## Commands

Run from the repo root (`local.properties` holds the Android SDK path):

- `./gradlew assembleDebug` — build debug APK
- `./gradlew assembleRelease` — build release APK (minify disabled)
- `./gradlew installDebug` — install on a connected device/emulator
- `./gradlew lint` — Android lint

## Architecture

- `CallBlockerService` is a `CallScreeningService` declared in the manifest with `BIND_SCREENING_SERVICE`. The OS calls `onScreenCall` for every incoming call; it allows the call if blocking is disabled, the number is missing, or the number is in contacts, otherwise it rejects the call (skipping the notification) and records it via `Prefs`. The service only works once the app holds `RoleManager.ROLE_CALL_SCREENING`, which `MainActivity` requests along with `READ_CONTACTS`.
- `ContactsLookup` uses `ContactsContract.PhoneLookup` for matching. Without contacts permission it returns false, so every call with a number would be blocked.
- `Prefs` is the only persistence: a `SharedPreferences` file holding the enable flag and a JSON-array blocked-call log (newest first, capped at 200 entries). The service and the UI (`MainActivity`, `LogsActivity` with `BlockedCallAdapter`) share state through it.
- UI uses ViewBinding (`buildFeatures.viewBinding`), RecyclerView and Material components. User-facing strings are in English in `res/values/strings.xml`.
