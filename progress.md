# E-Catalogs: progress

Snapshot of 2026-11-26, branch `feature/games-app-tests` (8 commits on top of `develop`, not pushed or merged),
app version 2.2 (versionCode 10). See `ECS.md` for how the project works; this file tracks where it stands.

Legend: ✅ done · 🟡 done but not verified / partly done · ⬜ not started · ⚠️ problem to look at

---

## 1. Where each area stands

| Area | Catalog | Details | Search | Favorites | Offline cache | Background sync | Notifications | Tests |
|---|---|---|---|---|---|---|---|---|
| Trending movies (TMDB) | ✅ + genre filter, upcoming, now playing | ✅ cast, trailer, providers, similar | ✅ | ✅ | ✅ | ✅ | ✅ + release reminders | 🟡 data layer only |
| Trending series (TMDB) | ✅ home screen, airing today | ✅ seasons sheet, episodes | ✅ | ✅ | ✅ | ✅ | ✅ | 🟡 data layer only |
| Movie reviews (NYT) | ✅ | ✅ dialog + "see the film" | ✅ | ✅ | ✅ | ✅ | ✅ | 🟡 |
| People (TMDB) | — | ✅ biography, credits | — | — | — | — | — | 🟡 mapping |
| NYT bestsellers | ✅ 5 genres, carousel | ✅ review, screen adaptations | ⚠️ screen commented out, API `TODO()` | ✅ | ✅ | ✅ (5 req/min throttle) | ✅ per genre | 🟡 |
| Gutenberg | ✅ language/topic filters | ✅ WebView reader (zoom + progress) | ✅ | ✅ | ✅ | ⬜ | ⬜ | 🟡 |
| Anime (Jikan) | ✅ browse rows, genres, random | ✅ extras, episodes, relations, characters | ✅ | ✅ | ✅ | ⬜ **(user will do it)** | ⬜ **(user)** | 🟡 |
| Anime schedule / people | ✅ weekly schedule, Jikan person | — | — | — | session only | — | — | 🟡 |
| Manga (Jikan) | ✅ featured rows, genres, random | ✅ recommendations, relations | ✅ | ✅ | ✅ | ⬜ **(user will do it)** | ⬜ **(user)** | 🟡 |
| **Video games (IGDB)**, new | 🟡 popular now, coming soon, recently released, genres | 🟡 | 🟡 | 🟡 | 🟡 | 🟡 | 🟡 coming-soon games | 🟡 queries, mapping, DataStore (not run) |
| Settings | ✅ account, notifications, sync, theme, about | | | | | | | ✅ VM + repository |
| Authentication (Firebase) | ✅ email, Google, One Tap, reset | | | | | | | ⬜ (empty example test) |
| Translation (Yandex) | ✅ shared `TextTranslator` in every area | | | | | | | ✅ |

---

## 2. Done 2026-11-25 (committed 2026-11-26 on `feature/games-app-tests`)

### 2.1 Project docs
- `ECS.md`: project guide covering build, modules, patterns, every area, tests, working rules and the
  findings in §5. Updated for games.
- `progress.md` (this file).

### 2.2 Video games feature (IGDB), 9 new modules
- `game/data_game/model` (JVM): `Game`, `GameDetails`, `GameGenre`, `GameUserData`, `UserGame`,
  `UserGameDetails`, `GameRecentSearchQuery`, `igdbImageUrl` / `IgdbImageSize`.
- `game/data_game/network`:
  - Twitch client-credentials token (`TwitchTokenProvider`, SharedPreferences `igdb_auth`, renewed a day
    early) and `IgdbAuthInterceptor` (headers, retry once on 401).
  - `IgdbRetrofitNetwork` (throttled to one request every 260 ms), `IgdbQueries` (Apicalypse).
  - Demo source with `assets/games.json` (5 games, no covers).
  - `isConfigured`: false when the credentials are missing.
- `game/data_game/database`: `GameDatabase` v1 (`igdb-database`): games with `catalog_position`, remote
  keys, recent searches.
- `game/data_game/data`:
  - `GameRemoteMediator` + shared `writeGamePage`, `GameRepository` (popular paging, search, details,
    `syncFirstPage`).
  - `GameBrowseRepository` (coming soon, recently released, genres, by-genre paging, `syncComingSoon`).
  - `UserGameDataRepository`, `CompositeGameRepository`, `GameRecentSearchRepository`.
- `game/screen_game/{common, game_catalog, game_details, game_search, game_favorites}`: all Compose.
  Paged lists are a RecyclerView `PagingDataAdapter` with ComposeView rows (`paging-compose` isn't in the
  Gradle cache). Strings in en / pt / ru.
- Wiring:
  - `settings.gradle.kts`, `app/build.gradle.kts` (4 screen modules), `nav_graph.xml`.
  - Drawer section "Video games → Games" (`ic_game`).
  - `MainActivity`: top-level destination, hidden toolbar on details and search, titles.
- DataStore proto: 25 `game_favorite_ids`, 26 `notifications_disabled_games`,
  27 `notified_coming_soon_game_ids` (**next free: 28**). `PreferencesDataSource`: `gameUserData`,
  `setGameFavoriteIds`, `notifiedComingSoonGameIds`. `ListCache`: `GAMES`, `GAMES_COMING_SOON`,
  `GAMES_RECENTLY_RELEASED`, `GAME_GENRES`.
- Sync and notifications:
  - `SyncWorker` runs `gameRepository.syncFirstPage()` then `gameBrowseRepository.syncComingSoon()`, both
    skipped without credentials.
  - New `NotificationCategory.GAMES` + Settings switch "Upcoming games".
  - `Notifier.postComingSoonGamesNotification`, channel `games`, deep link `com.rick.ecs://game_catalog`.
  - `TestNotifier` updated.
- Tests added (**not run**): `IgdbImageTest`, `IgdbQueriesTest`, `GameMappersTest`, and DataStore tests for
  game favorites, game notifications and the coming-soon ids.

**Before using it:**
- [ ] Register a Twitch app (dev.twitch.tv/console), then add `igdbClientId` and `igdbClientSecret` to
  `local.properties`.
- [ ] Build (prod and demo) and fix any compile errors. Nothing has been compiled yet.
- [ ] Run the unit tests (`./gradlew testDemoDebugUnitTest`).
- [ ] Commit the generated `game/data_game/database/schemas/…GameDatabase/1.json`.
- [ ] Check on a device: drawer entry, catalog, genre chips, details (trailer, IGDB link), search,
  favorites + undo, Settings switch, a sync run (the first sync records "coming soon"; the next one notifies).
- [ ] Decide on the popularity ranking (`IgdbQueries.POPULARITY_TYPE_VISITS`; alternatives: playing, want to play).
- [ ] ⚠️ The client secret ships in the APK. Move the token request to a backend before a public release.

### 2.3 App instrumented tests (later in the session; written, not compiled or run)
- **Production changes:**
  - `AuthSession` + `FirebaseAuthSession` + `AuthModule` in `auth_data`. `AuthenticationFragment` (start check, One
    Tap) and `SettingsFragment` (sign out, delete) use it; `settings/screen_settings` now depends on `auth_data`
    instead of Firebase.
  - Games catalog: the scroll-to-top moved from collecting `selectedGenre` (replayed on every return from details,
    so the list jumped to the top) into the genre chip's click.
- **Test setup in `:app`:** `compose-ui-test` added to the catalog; androidTest deps for Espresso core, test
  core/rules, work-testing, Compose ui-test, `auth_data`, `ui_components:auth`, `datastore_test` (all cached).
  `TestAuthSession`/`TestAuthModule`, `TestDataStoreModule`, `AppTestExtensions`, `CatalogRobot`.
- **Tests (behaviour agreed with the user):** `NavigationTest` (10), `LoginScreenTest` (10), `SettingsTest` (8),
  `CatalogFlowTest` × 7 XML areas (5 each; bestseller search skipped), `GamesCatalogTest` (6).
- **Run on the API 37 emulator (2026-11-25 18:27): 77 tests, 75 passed, 1 failed, 1 skipped** (bestseller search).
  - [ ] Still failing, a real bug: Back on top-level screens should go home (`NavigationTest.severalTopLevelScreens_…`).
    The drawer's `popUpTo(startDestination)` targets the auth screen, which pops itself, so screens stack up.
  - Fixed along the way (the tests found them):
    - Movie review search showed the activity toolbar over its own (missing from MainActivity's hide list).
    - Bestsellers lost their scroll after details (NestedScrollView without an id, grid filled in late): restored
      in `BestsellerFragment.restoreScroll` once the page is tall enough.
    - Demo `search_movie_article.json` was an old NYT response (`multimedia` list, `meta`): converted, so review
      search works in demo; `ArticleRepositoryImpl.searchArticle` now logs decoding errors instead of ending silently.
  - Not a bug after all: the soft keyboard's search key works (TextView turns an unhandled IME action into Enter).
- Build changes: Espresso 3.7.0 (3.6.1 crashes on API 36+), recyclerview 1.2.1 for androidTest.

### 2.4 Encrypted Twitch token
- `KeystoreCipher` (`:data:common`): AES-256-GCM key in the Android Keystore, no new dependency.
- `TwitchTokenProvider` stores `access_token_encrypted`; migrates the old plaintext `access_token`; refetches when
  it can't decrypt. `KeystoreCipherTest` (6, instrumented) passes.

---

## 3. Earlier work (committed 2026-11-26 on `feature/games-app-tests`, split by area)

Done before 2026-11-25:
- **Translation refactor:** the per-area `GetTranslationUseCase` and `TranslationViewModel` (anime, book,
  movie) are deleted. A shared `TextTranslator` (batched, LRU, per language), `TranslationToggle` (XML) and
  Compose `LocalTextTranslator` replace them.
- **Database migrations:** Jikan v1→2, Movie v1→2 (`catalog_position`), Book v1→2→3 (bestseller table
  re-key, Gutenberg `catalog_position`). Schema JSONs are added.
- **Catalog caching:** `catalog_position` pattern (search results never replace catalog rows, favorites
  survive refreshes), `ListCache` freshness per list, `CatalogPosition.kt`.
- **Network monitoring:** `NetworkMonitor` / `ConnectivityManagerNetworkMonitor`, an offline snackbar in
  `MainActivity`, `TestNetworkMonitor`.
- **Test setup (NiA style):** `EcsTestRunner` (HiltTestApplication), `AndroidTestConventionPlugin`,
  `Testing.kt` (`allowModulesWithoutTests`), `app/src/androidTest`, many data/model/screen unit tests with
  JSON resources and Robolectric DAO tests.
- **Baseline profiles + macrobenchmarks:** `:benchmarks` module, 4 profile journeys, startup and scroll
  benchmarks. Profiles are generated in `app/src/prodRelease/generated/baselineProfiles/`, with dex layout
  optimization on.
- **New UI (Compose in existing screens):** anime schedule, Jikan person screen, hero carousel
  (`EcsHeroCarousel`, `useHeroCarousel`), browse headers, `FilmLink` ("see the film" from a review),
  broadcast-time conversion, Jikan relations, screen adaptations for books, shared Glide/Coil image cache.
- **Build and release:**
  - AGP 9.3 / Kotlin 2.3 / Gradle 9.x, the baseline profile plugin.
  - Release signing from `local.properties`, R8 + resource shrinking with keep rules, Crashlytics mapping
    upload, 16 KB page-size fixes, version 2.2 / 10.
- Many layouts, strings (en/pt/ru) and themes touched across all areas.

**Last verified run (before this session):**
- All 4 baseline profiles generated (Medium Phone AVD, 2026-11-25 10:21).
- `EcsTestRunnerTest` 2/2 passed.
- 84 unit tests in 17 classes passed, 0 failures.
- Benchmarks were **skipped**, so no startup/scroll numbers yet.

---

## 4. History (committed)

759 commits since 2022-05-08. Recent milestones:
- 2026-11-24: feature improvements · 2026-11-23: periodic background sync (movies, series, articles); API keys
  moved from JNI to BuildConfig; first-page sync; AGP 9.3 / Kotlin 2.3 / Gradle 9.6; sized TMDB images;
  notifications + sync scaffolding; package names normalized to `com.rick.ecatalogs.<module path>`;
  splash animation fix.
- 2026-11-20: transaction fix for a long-standing bug; NDK key files untracked.
- 2026-06-29: release update.
- 2026-04: edge-to-edge + display cutouts, 16 KB fix, auth UI redesign, test release v.7.
- 2026-03: dark mode (dark-first theme), translations (pt, ru), common theming module, favorites bug fix.

---

## 5. Open items and known problems (whole project, including parts not touched in this session)

### To do (planned)
- [ ] **Anime & manga background sync + notifications**: the user will do this. The proto already reserves
  `notified_trending_anime_ids` (12) and `notified_trending_manga_ids` (13). It needs `syncFirstPage()` in
  the anime/manga repositories, `SyncWorker`, new `NotificationCategory` values, `notifications_disabled_*`
  fields (28+), Settings labels, `Notifier` methods and `TestNotifier`.
- [ ] **Gutenberg** is not in the background sync either.
- [ ] **Bestseller search**: `BestsellerRetrofitNetwork.searchBestseller` and
  `DemoBestsellerNetworkDataSource` are `TODO()`, and the `bestseller_search` screen code is fully commented out.
- [ ] "Favorites" drawer entries for moving pictures and books are commented out in `nav_menu.xml`.
- [ ] App-level unit/screenshot tests are commented out in `app/build.gradle.kts` (Robolectric, Roborazzi,
  hilt-testing, work-testing).
- [ ] Most screen modules have no tests of their own (the app UI tests in §2.3 cover the flows). `auth_screen` has only an empty `ExampleInstrumentedTest`.
- [ ] Games have no Robolectric mediator/repository tests yet (the other data modules have them).
- [ ] Run the macrobenchmarks (`:benchmarks:connectedProdBenchmarkReleaseAndroidTest`) to measure the baseline
  profile's effect. Consider a games journey for the profile (list id `game_recycler_view`).
- [ ] Regenerate the baseline profiles after the games feature lands.
- [ ] Jacoco: the combined report misses `src/debug`, `src/prod`, `src/demo`… (TODO in `Jacoco.kt`).
- [ ] README is out of date: it still says keys are secured with NDK/JNI and series are "Top 150 IMDB" and
  promises a universal search screen. Games and the new features aren't mentioned.
- [ ] First `dependencyGuard` run needs a baseline (`./gradlew dependencyGuardBaseline`); none is committed.
- [ ] Lint uses a per-module `lint-baseline.xml`, but none exist yet. The first lint run creates them.
- [ ] Optional: a `CLAUDE.md` containing `@ECS.md`, so the guide loads automatically.

### Possible bugs (found while reading; not fixed)
- ⚠️ `trending_series_details` nav graph uses the id `movie_screen_movie_trending_movie_details_nav_graph`,
  the same as the movie details graph.
- ⚠️ The series-favorite deep link `com.rick.ecs/trending_series_favorite_fragment` is missing `://`.
- ⚠️ `TranslationNetwork` sends `Authorization: ": Api-Key <key>"`. Yandex expects `Api-Key <key>`.
- ⚠️ `SyncManager.requestSync()` is never called; if used for a "sync now" button it would be skipped while
  the last sync is recent.
- ⚠️ IGDB secret in the APK (see §2.2).
- ⚠️ `ArticleDetailsDialogFragment` has constructor arguments, so it can't be recreated (rotation while open).
- ⚠️ Back from top-level screens: see §2.3.

### Housekeeping before committing
- [ ] Commit the generated baseline profiles (`app/src/prodRelease/generated/baselineProfiles/*.txt`, ~6 MB).
- [ ] Don't commit `.idea/deploymentTargetSelector.xml`, `.idea/gradle.xml`,
  `.idea/androidTestResultsUserPreferences.xml` (IDE-local).
- [ ] Add `.DS_Store` to `.gitignore` (`benchmarks/.DS_Store`, `benchmarks/src/.DS_Store` are untracked).
- [ ] `google-services.json` is tracked in `app/` **and** `authentication/auth_screen/`. Check that the second
  one is needed.
- [ ] Release signing: set `releaseStoreFile`, `releaseStorePassword`, `releaseKeyAlias`, `releaseKeyPassword`
  in `local.properties`, otherwise release builds use the debug key. `EcsKey.jks` and `upload_certificate.pem`
  are in the root and gitignored.
- [x] Split into commits by area on `feature/games-app-tests`; game modules got their `.gitignore`.
- [ ] Merge `feature/games-app-tests` into `develop` (fast-forward) and push.
- [ ] Run `./gradlew testDemoDebugUnitTest` (the game unit tests have never run).
