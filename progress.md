# E-Catalogs: progress

Snapshot of 2026-11-27, branch `feature/games-app-tests` (9 commits on top of `develop`, not pushed or merged;
§2.0 is uncommitted), app version 2.2 (versionCode 10). See `ECS.md` for how the project works; this file tracks where it stands.

Legend: ✅ done · 🟡 done but not verified / partly done · ⬜ not started · ⚠️ problem to look at

---

## 1. Where each area stands

| Area | Catalog | Details | Search | Favorites | Offline cache | Background sync | Notifications | Tests |
|---|---|---|---|---|---|---|---|---|
| Trending movies (TMDB) | ✅ + genre filter, upcoming, now playing | ✅ cast, in-app trailer + gallery, providers, similar | ✅ | ✅ | ✅ | ✅ | ✅ + release reminders | 🟡 data layer only |
| Trending series (TMDB) | ✅ home screen, airing today | ✅ seasons sheet, episodes, in-app trailer + gallery | ✅ | ✅ | ✅ | ✅ | ✅ | 🟡 data layer only |
| Movie reviews (NYT) | ✅ | ✅ details screen + "see the film" | ✅ | ✅ | ✅ | ✅ | ✅ | 🟡 |
| People (TMDB) | — | ✅ biography, credits | — | — | — | — | — | 🟡 mapping |
| NYT bestsellers | ✅ 5 genres, carousel | ✅ review, screen adaptations | ⚠️ screen commented out, API `TODO()` | ✅ | ✅ | ✅ (5 req/min throttle) | ✅ per genre | 🟡 |
| Gutenberg | ✅ language/topic filters | ✅ WebView reader (zoom + progress) | ✅ | ✅ | ✅ | ⬜ | ⬜ | 🟡 |
| Anime (Jikan) | ✅ browse rows, genres, random | ✅ extras, episodes, relations, characters, in-app trailer | ✅ | ✅ | ✅ | 🟡 top anime, first page | 🟡 new in top anime | 🟡 |
| Anime schedule / people | ✅ weekly schedule, Jikan person | — | — | — | session only | — | — | 🟡 |
| Manga (Jikan) | ✅ featured rows, genres, random | ✅ recommendations, relations | ✅ | ✅ | ✅ | 🟡 top manga, first page | 🟡 new in top manga | 🟡 |
| **Video games (IGDB)**, new | 🟡 popular now, coming soon, recently released, genres | 🟡 | 🟡 | 🟡 | 🟡 | 🟡 | 🟡 coming-soon games | 🟡 queries, mapping, DataStore (not run) |
| Settings | ✅ account, notifications, sync, theme, about | | | | | | | ✅ VM + repository |
| Authentication (Firebase) | ✅ email, Google, One Tap, reset | | | | | | | ⬜ (empty example test) |
| Translation (Yandex) | ✅ shared `TextTranslator` in every area | | | | | | | ✅ |

---

## 2.0 Done 2026-11-27 (uncommitted)

### In-app trailers (new module `:data:trailer`)
- **Player:** android-youtube-player `core` 13.1.0 (YouTube's IFrame player in a WebView; YouTube allows no
  other way to play its videos). `YouTubePlayerHost` (Compose `AndroidView`, inflated from XML with automatic
  initialization off), follows the lifecycle, pauses when hidden.
- **Details header gallery** (`MediaGallery`, `MediaGalleryHost` for XML screens): a swipeable pager with the
  trailer first (YouTube thumbnail + play, plays in place), then the poster, then (movies/series) up to 6 TMDB
  backdrops shown whole over a blurred copy. Page dots ("Page 2 of 3" for TalkBack). Keeps its page and the
  video position on rotation; the player's fullscreen button opens the trailer screen at the same second.
  Videos whose owner blocks embedding show "Open in YouTube".
- **Trailer screen** (`TrailerActivity`): black, title, close, "Open in YouTube"; immersive in landscape;
  handles rotation itself, so the video never reloads.
- **Where:** movie and series details (the series header now matches the movie header: 450dp card), anime
  details (gallery + the "Trailer" link), game details (trailer first in the media row). Every "Watch trailer"
  opens the trailer screen instead of the YouTube app.
- **TMDB backdrops:** `images` in `append_to_response` with `include_image_language=en,null`;
  `TmdbDetailsExtras.backdrops` (textless first, best voted, max 6).
- **Demo data:** trailers (checked with YouTube oEmbed: official uploads, embeddable) for the demo movie, series,
  The Witcher 3 and Elden Ring; the demo movie/series backdrop.
- **Tests:** unit `YouTubeTest` (3), `DetailsGalleryTest` (2) pass; backdrop mapping in `TmdbDetailsExtrasTest`
  written but not run (that module's tests don't compile, see §5, and Robolectric's runtime jar isn't cached).
  Instrumented (6 new): gallery starts on the trailer and swipes to the poster (movies, series); "Watch trailer"
  opens the trailer screen (movies, series, games; launches are intercepted with an `ActivityMonitor`, so
  nothing plays); the game media row starts with the trailer.
- **Run on the API 37 emulator (2026-11-27): movies, series, anime, games, login, navigation, settings: 55 tests,
  54 passed**; the failure is the known Back-to-home bug (§2.3).
- Not checked by hand yet: playing a trailer (inline, fullscreen, rotation, "Open in YouTube"), dragging the
  player's seek bar inside the swipeable gallery, light/dark.

### Anime & manga background sync + notifications (written, not compiled or run)
- `AnimeRepository`/`MangaRepository.syncFirstPage()`: the first page of top anime/manga, stored only when its ids
  changed (shared `writeAnimePage`/`writeMangaPage`, also used by the remote mediators), marks the list fresh, returns
  what's new since the last sync (nothing on the first one). `firstPageIds()` in the remote-keys DAOs (no schema change).
- Proto: `notified_trending_anime_ids` (12) / `notified_trending_manga_ids` (13) now used; new
  `notifications_disabled_anime` (28) and `_manga` (29); next free is 30.
- `NotificationCategory.ANIME`/`MANGA`, Settings switches "Popular anime"/"Popular manga", channels `anime`/`manga`,
  notifications "New in popular anime/manga" opening `com.rick.ecs://anime_catalog` / `manga_catalog` (new deep links).
- `SyncWorker` runs both in parallel with the rest. `TestNotifier` records them.
- Tests: 5 sync tests in `JikanRemoteMediatorsTest` (Robolectric), 2 in `PreferencesDataSourceTest`; `SettingsTest`
  covers the two new switches.

### CI (GitHub Actions; written, not run yet)
- `.github/workflows/build.yml` replaces the broken `pull_request.yml` (invalid step, `testDebug` typo, `master`).
  See ECS.md "CI". `.github/ci-gradle.properties` from NiA (minus isolated projects).
- Coverage switched on for debug builds (NiA's `configureJacoco(commonExtension, …)`); before, the combined report
  had no data.
- Fixed unit tests that didn't compile: `stateWith()` in the movie, Gutenberg and Jikan mediator tests inferred
  `PagingState<Nothing, …>`.
- New local branch `main` (from `develop`, 9af01a8e), not pushed.
- [ ] Before the first run: commit `app/lint-baseline.xml` and `app/dependencies/` (generated locally).
- [ ] The Back-to-home `NavigationTest` failure will make the instrumented job red until the bug is fixed.

### CD to Google Play (written, not run yet)
- `.github/workflows/release.yml`: tag `v2.3.0` on main → signed `bundleProdRelease` → Play internal testing (+ R8
  mapping, release notes) → GitHub release (notes only). Version name/code from the tag. See ECS.md "CD".
- `app/build.gradle.kts` reads `ecsVersionCode`/`ecsVersionName` (fallback 10 / "2.2").
- `distribution/whatsnew/whatsnew-en-US`: draft notes for 2.3; edit before tagging.
- [ ] Ricardo: service account + Play permission; GitHub environment `play` with the secrets; push `main`;
  commit the baseline profiles; dry run from the Actions tab.

### Design consistency (written, not compiled or run)
- **Games:** details and search use the app's back arrow (`EcsBackButton`); the details favorite is the glass one
  (`EcsGlassFavoriteButton`); search looks like the XML searches (plain field in the bar, `EcsSearchResultCard`
  with the game's cover, summary with "Show translation", year · rating). The Compose favorites cards use the
  shared glass button.
- **Games, second pass** (after Ricardo's review): the list rows (catalog, favorites) put the small glass star on the
  cover, like book covers, instead of an orange star beside the text (`EcsGlassFavoriteButton(small = true)`); the
  search bar has the XML toolbar's orange search action instead of a clear button; the search card's bottom line is
  the XML one (facts left, "Show translation" right, via the shared `rememberTranslationToggle`).
- **Fixed:** the movie, series and manga searches showed the star filled when the title was *not* a favorite.
- **Settings:** the page now pads for the navigation bar; the About section (links, version) was hidden under it.
- **Bestsellers:** the NYT lists are a chip row under "Top picks" (as the movie/series genre chips), instead of
  icon tiles above it; labels translated (EN/PT/RU). Removed the tile adapter, its layout, the 5 category images
  and the `data_ui_design_category_tile` background.
- **Movie reviews:** tapping a review opens a details screen (`ArticleDetailsFragment`) in the bestseller details
  style instead of the dialog; the dialog, its layout and its 4 strings are gone. `MovieReviewsCatalogTest` now
  favorites from the details toolbar.

### Other
- **Rotation:** removed `android:screenOrientation="portrait"` from `<application>` (ignored there anyway).
  Tablet layouts are the next step.
- **Login screen redesign:** brand header, card with a Sign in / Create account switch, outlined fields, error
  banner, password mismatch hint, Google button in both modes; new slogan "Your next favourite story starts
  here" (EN/PT/RU). `TextInputRoundRect` removed; reset dialog is a Material 3 `AlertDialog`.
- **Small visual fixes (not compiled or run yet):** games' favorite button and the Compose favorites cards use
  the app's star instead of a heart; the login content is centred vertically (still scrolls when taller);
  drawer/toolbar titles "Trending Movies/Series" are now "Movies"/"Series" (EN/PT/RU; notification and
  Settings labels still say "trending", since those notify about trending titles).
- **Release crash fix:** R8 resource shrinking removed Crashlytics' `mapping_file_id` string; kept with
  `app/src/main/res/raw/keep.xml`. Checked 2026-11-27: `assembleProdRelease -PskipMappingUpload` passes (lint
  clean) and the string is in the APK. The YouTube player survives R8 too (view class name and the JavaScript
  bridge methods kept, player HTML present). Not yet run on a device.

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
- [ ] Tablet layouts: wider grids, list-detail, navigation rail (rotation is unlocked; screens are phone-sized).
- [ ] README: add in-app trailers once committed.
- [ ] `TrendingMovieRemoteMediatorTest` doesn't compile (`emptyState()`/`stateWith()` infer
  `PagingState<Nothing, …>`), so `:movie:data_movie:data` unit tests can't run. Found 2026-11-27.
- [ ] Optional: a `CLAUDE.md` containing `@ECS.md`, so the guide loads automatically.

### Possible bugs (found while reading; not fixed)
- ⚠️ `trending_series_details` nav graph uses the id `movie_screen_movie_trending_movie_details_nav_graph`,
  the same as the movie details graph.
- ⚠️ The series-favorite deep link `com.rick.ecs/trending_series_favorite_fragment` is missing `://`.
- ⚠️ `TranslationNetwork` sends `Authorization: ": Api-Key <key>"`. Yandex expects `Api-Key <key>`.
- ⚠️ `SyncManager.requestSync()` is never called; if used for a "sync now" button it would be skipped while
  the last sync is recent.
- ⚠️ IGDB secret in the APK (see §2.2).
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
