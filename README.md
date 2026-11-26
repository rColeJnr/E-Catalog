# E-Catalogs

**Available on Google Play:** [E-Catalogs on the Play Store](https://play.google.com/store/apps/details?id=com.rick.moviecatalog)

<a href="https://play.google.com/store/apps/details?id=com.rick.moviecatalog"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="80"/></a>

E-Catalogs is an Android app for discovering movies, TV series, books, anime, manga and video games, built on five
public APIs. It has been in development since 2022 and is published on Google Play.

## What's in the app
| Area | Source | Highlights |
|---|---|---|
| **Movies & TV series** | [TMDB](https://www.themoviedb.org/) | Trending, upcoming and now playing, genre filters, cast, trailers, where to watch, similar titles, seasons and episodes |
| **Movie reviews** | [New York Times](https://developer.nytimes.com/) | Critics' reviews, with a link to the film they review |
| **Books** | [Project Gutenberg](https://gutendex.com/) and NYT Best Sellers | Free books with a built-in reader, best-seller lists by genre, screen adaptations |
| **Anime & manga** | [Jikan](https://jikan.moe/) (MyAnimeList) | Top lists, genres, weekly schedule, characters and people, relations |
| **Video games** | [IGDB](https://www.igdb.com/) | Popular now, coming soon, recently released, genres (built entirely in Jetpack Compose) |

In every area: search with recent queries, favorites, and translation of descriptions (Portuguese and Russian).
The app also has:
- **Offline-first catalogs**: lists are cached in Room and paged with Paging 3, so they load instantly and work without a connection.
- **Background sync and notifications**: WorkManager refreshes the trending lists on a schedule you choose and notifies you about new titles, with a switch per category in Settings.
- **Accounts**: email, Google and One Tap sign-in (Firebase Auth).
- **Light and dark themes**, in English, Portuguese and Russian.

## Tech stack
- **Language and UI**: Kotlin, XML/ViewBinding screens moving to Jetpack Compose, a shared design system module.
- **Architecture**: Clean Architecture with MVVM, multi-module by feature (data and screen modules per area).
- **Data**: Retrofit (Gson, Kotlinx Serialization), Room with migrations, Paging 3 with RemoteMediator, Proto DataStore.
- **DI and background work**: Hilt, WorkManager.
- **Security**: API keys come from `local.properties` into `BuildConfig` (never committed); the IGDB token is stored
  encrypted with an AES-256-GCM key in the Android Keystore.
- **Build**: Gradle version catalog and convention plugins (`build-logic`), R8, baseline profiles, Firebase Crashlytics.
- **Tests**: unit and Robolectric tests for the data layer, and 77 instrumented UI tests (Espresso, Compose UI Test)
  covering navigation, login, settings and every catalog flow.

## Getting started
The **demo** flavor uses bundled sample data, so it runs without any API key:
```bash
./gradlew :app:installDemoDebug
```

The **prod** flavor calls the real APIs. Add your own keys to `local.properties` in the project root (git-ignored):
```properties
tmdbApiKey=YOUR_TMDB_KEY              # themoviedb.org/settings/api
nyTimesApiKey=YOUR_NYT_KEY            # developer.nytimes.com (Article Search and Books APIs)
translationApiKey=YOUR_TRANSLATION_KEY
igdbClientId=YOUR_TWITCH_CLIENT_ID    # dev.twitch.tv/console
igdbClientSecret=YOUR_TWITCH_SECRET
```

Run the tests:
```bash
./gradlew testDemoDebugUnitTest                  # unit tests
./gradlew :app:connectedDemoDebugAndroidTest     # UI tests (needs a device or emulator)
```

## Project structure
- `:app`: single activity, navigation drawer, app-wide wiring.
- `:movie`, `:book`, `:anime`, `:game`: one area each, split into `data_*` (model, network, database, data) and
  `screen_*` (one module per screen).
- `:settings`, `:authentication`: settings and sign-in.
- `:data`: shared modules (design system, UI components, DataStore, sync, notifications, translation, testing).
- `:benchmarks`: baseline profile generation and startup and scrolling benchmarks.
- `build-logic`: Gradle convention plugins.

## Screenshots
Screenshots from an earlier version:

![movies](https://user-images.githubusercontent.com/72414394/196361471-b9792801-538a-4dc3-8b26-594a65700eb4.jpg)
![series](https://user-images.githubusercontent.com/72414394/196361540-6284ff87-19ad-450b-bb65-fa45ab31534c.jpg)
![books](https://user-images.githubusercontent.com/72414394/196361561-6b6faab0-d561-4e39-9e72-caf7d683bc23.jpg)
![manga](https://user-images.githubusercontent.com/72414394/196361594-7cde645e-d57f-4c2c-afc6-a5995280e71d.jpg)
![anime](https://user-images.githubusercontent.com/72414394/196361607-d7abfb6d-d50f-4863-928c-4ef84d72d475.jpg)
![search](https://user-images.githubusercontent.com/72414394/196361636-01454c3d-95f1-4c96-affa-ac8975db6478.jpg)
![nav drawer](https://user-images.githubusercontent.com/72414394/196361646-53710978-fabb-4b8a-938f-560a05866f60.jpg)
