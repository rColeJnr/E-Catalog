# E-Catalogs project guide

E-Catalogs (`rootProject.name = "ecatalogs"`, applicationId `com.rick.moviecatalog`, v2.2 / code 10)
is a multi-module Android app with four content areas: **movies & series** (TMDB + NYT movie
reviews), **books** (Project Gutenberg + NYT bestsellers), **anime & manga** (Jikan / MyAnimeList)
and **video games** (IGDB).
Users sign in with Firebase, keep favorites, and can translate content (pt, ru).

The architecture and build follow Now in Android (`../nowinandroid`); copy its patterns when adding infra.

## Build

- AGP 9.3.2, Kotlin 2.3.0, KSP 2.3.4, Gradle 9.x with the configuration cache and build cache on.
- compileSdk 37, targetSdk 36, minSdk 24 (benchmarks: 28). Java/JVM target 11 with core-library
  desugaring. build-logic itself compiles with JVM 17.
- `-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi` everywhere; `-PwarningsAsErrors=true` is opt-in.
- Dependencies live in `gradle/libs.versions.toml`. Some versions are pinned on purpose (see comments there):
  - Navigation runtime 2.7.7 (2.10 pulls Compose runtime 1.11 → startup crash); Safe Args plugin 2.10.0.
  - UiAutomator 2.4.0-alpha07 (already cached; metered connection).
  - DataStore 1.3.0 (16 KB page-size aligned); Jacoco 0.8.14 (JDK 21).
- Typesafe project accessors: `projects.data.uiComponents.common` etc.
- `android.enableR8.fullMode=false`. Release has R8 + resource shrinking; `-PminifyWithR8=false` turns it off.
- Release signing reads `releaseStoreFile`, `releaseStorePassword`, `releaseKeyAlias` and
  `releaseKeyPassword` from `local.properties`. Without them it falls back to the debug key.
  `EcsKey.jks`, `*.pem` and `local.properties` are gitignored.
- Crashlytics uploads the release mapping file. Pass `-PskipMappingUpload` to skip it when offline.
- Optional: `-PenableComposeCompilerMetrics=true` / `-PenableComposeCompilerReports=true`.

### Flavors and build types
- Flavor dimension `contentType`: `demo` (`.demo` suffix, local static data) and `prod` (real backends).
  Every library module gets the same flavors (`configureFlavors`).
- Build types: `debug` (`.debug` suffix) and `release`.

### Convention plugins (`build-logic/convention`)
| id | does |
|---|---|
| `ecatalogs.android.application` | AGP app + lint + dependency-guard, targetSdk 36 |
| `ecatalogs.android.library` | AGP library + lint + flavors; **resource prefix from the module path** (`:movie:screen_movie:common` → `movie_screen_movie_common_`); androidTest only when `src/androidTest` exists |
| `ecatalogs.android.feature` | library + hilt + navigation; viewBinding; coroutines, material, swiperefresh, glide, lifecycle; runner `EcsTestRunner`; `:data:testing` for tests |
| `ecatalogs.android.feature.data` | library + hilt + room; retrofit/gson, okhttp + logging, paging, `:data:common` |
| `ecatalogs.android.hilt` | KSP + Hilt |
| `ecatalogs.android.room` | Room + KSP, schemas in `<module>/schemas` |
| `ecatalogs.android.navigation` | Safe Args + navigation fragment/ui |
| `ecatalogs.android.paging` | paging common/runtime |
| `ecatalogs.android.library.compose` / `application.compose` | Compose BOM, material3, icons, constraintlayout-compose; optional `compose_compiler_config.conf` |
| `ecatalogs.android.application.firebase` | google-services, perf, crashlytics (protobuf excluded from Firebase perf) |
| `ecatalogs.android.application.flavors` | demo/prod flavors on the app |
| `ecatalogs.android.*.jacoco` | `create<Variant>CombinedCoverageReport` |
| `ecatalogs.android.lint` | xml + sarif reports, `checkDependencies`, per-module `lint-baseline.xml` |
| `ecatalogs.android.test` | `com.android.test` modules (benchmarks) |
| `ecatalogs.jvm.library` | pure Kotlin JVM modules |

All of them call `allowModulesWithoutTests()`, so modules with no tests don't fail `test`.

### Custom lint (`:lint`)
- `DesignSystem` (error): use `EcsTheme` instead of `MaterialTheme` / `darkColorScheme` /
  `lightColorScheme` (allowed only in `EcsTheme.kt`); no `android.R.color.holo_blue_dark` / `darker_gray`.
- `TestMethodPrefix` (warning): test names must not start with `test`.
- `TestMethodFormat` (warning, androidTest only): `given_when_then` or `when_then`.

## App shell (`:app`)
- Single activity: `MainActivity` (AppCompat, View-based) with a `DrawerLayout` + `NavigationView`
  (`res/menu/nav_menu.xml`) and one `NavHostFragment` on `res/navigation/nav_graph.xml`.
  The root graph starts at `auth_graph` and `<include>`s each feature's graph.
- Top-level destinations (hamburger instead of back) are listed in `appBarConfiguration`. The activity
  toolbar is hidden on search, details, reader, person, schedule and auth screens, which have their own.
  Toolbar titles are a `when (destination.id)` in `MainActivity`. **Add new screens to both lists.**
- Destination ids carry the module resource prefix, e.g.
  `com.rick.ecatalogs.movie.screen_movie.trending_movie_catalog.R.id.movie_screen_movie_trending_movie_catalog_trendingmoviefragment`.
- `MainActivityViewModel`: `uiState` from `UserSettingsDataRepository.userData` (user name, theme,
  notification prefs) and `isOffline` from `NetworkMonitor` (shows an indefinite Snackbar).
- Theme: dark-first. `ECatalogsApplication` applies the last night mode from SharedPreferences
  `main_activity` / `night_mode` before the first frame; `MainActivity.applyTheme` updates it.
- `ECatalogsApplication.onCreate`: `installImageCaching` (shared Glide + Coil disk cache), night mode,
  `Sync.initialize`.
- Asks for POST_NOTIFICATIONS once (API 33+) if any notification category is enabled.
- Notification taps are deep links handled in `onNewIntent` → `navController.handleDeepLink`.
- R8 rules (`app/proguard-rules.pro`): Gson models are kept by package (`**.network.model.**`, `**.model.**`),
  plus TypeToken subclasses, Retrofit interfaces, worker names, Parcelable/Serializable names, protobuf
  fields. **New Gson-parsed classes must live in a `model` package, or need their own keep rule.**
- Firebase analytics is off by default and turned on in `src/prod/AndroidManifest.xml`.
- `dependencyGuard` checks `prodReleaseRuntimeClasspath`.
- Excludes `libandroidx.graphics.path.so` (not 16 KB page-aligned).

## Baseline profiles and benchmarks (`:benchmarks`)
- A `com.android.test` module targeting `:app`. It runs on a **connected device or emulator**
  (`useConnectedDevices = true`; no Gradle managed device, to avoid a ~1 GB download). API 28+.
- `PACKAGE_NAME` = `com.rick.moviecatalog` + the flavor suffix (prod has none, demo `.demo`).
- Journeys start through the deep link `com.rick.ecs://trending_series_catalog`. List ids:
  `recyclerView` (trending, book screens), `jikan_recycler_view` (anime/manga); card id `movie_entry_card_view`.
  Drawer items are opened by their **English** title ("Anime", "NY Times Bestsellers"…).
- Profiles: `StartupBaselineProfile` (also writes the startup profile), `MoviesBaselineProfile`,
  `BooksBaselineProfile`, `AnimeBaselineProfile`.
  Generate: `./gradlew :app:generateProdReleaseBaselineProfile`. Output (commit it):
  `app/src/prodRelease/generated/baselineProfiles/{baseline,startup}-prof.txt`.
  `automaticGenerationDuringBuild = false`, `dexLayoutOptimization = true`.
- Benchmarks: `StartupBenchmark` (cold; None / Partial with the profile disabled / Partial with the
  profile required / Full), `ScrollTrendingSeriesBenchmark` (warm, frame timing).
  Run: `./gradlew :benchmarks:connectedProdBenchmarkReleaseAndroidTest`.
- The manifest has `<profileable android:shell="true">`.

## Shared modules (`:data:*`)
- **common**: `@Dispatcher(EcsDispatchers.IO/Default)`, `@ApplicationScope` CoroutineScope,
  `Resource<T>` (Success/Error/Loading), `JsonParser`/`GsonParser` (used by Room converters),
  `String.toDateFormat()` / `Date.toDateString()`.
- **util**: interfaces `NetworkMonitor` (impl `ConnectivityManagerNetworkMonitor`, which tracks every
  network with internet), `SyncManager`, `ReleaseReminderScheduler`.
- **analytics**: `AnalyticsHelper`. demo → `StubAnalyticsHelper` (logcat), prod → `FirebaseAnalyticsHelper`;
  `LocalAnalyticsHelper` for Compose.
- **datastore_proto**: `user_preferences.proto` (proto3, lite). **The next free field number is 30.**
  Boolean settings are stored negated (`sync_disabled`, `notifications_disabled_*`) so the default means enabled.
- **datastore**: `PreferencesDataSource` (favorites per catalog as `map<id,bool>`, user name, theme
  0 = dark/1 = light/2 = system, sync on/interval, notification categories, `last_sync_epoch_millis`,
  `list_refreshed_at`, release reminders, the ids from the last notification run).
  - `ListCache` + `CachedList` keys: screens reuse the stored list while it is younger than the sync
    interval (`isFresh`), or from the same day (`isFreshToday`, for "airing today").
    `markRefreshedExclusive(list, prefix)` keeps only one filtered variant fresh (e.g. Gutenberg filters).
- **datastore_test**: `InMemoryDataStore<T>`.
- **sync** (WorkManager + Hilt workers through `DelegatingWorker`):
  - `Sync.initialize` enqueues the one-time `SyncWorkName` (KEEP, skipped when the last sync is newer than
    the interval) and the periodic `PeriodicSyncWorkName` (KEEP). **Don't rename these work names.**
  - `SyncWorker` syncs in parallel: trending movies, trending series and articles (`syncFirstPage()`)
    and bestsellers (`syncAllGenres()`), plus games (`GameRepository.syncFirstPage()` then
    `GameBrowseRepository.syncComingSoon()`, one after the other because of the IGDB rate limit; both skip
    themselves when the IGDB credentials aren't set), and the first page of top anime and top manga
    (`AnimeRepository`/`MangaRepository.syncFirstPage()`; in parallel, the Jikan client spaces its own requests).
    It then posts notifications for the enabled categories, records
    the sync time on success and retries on failure. It does nothing when sync is turned off.
  - `WorkManagerSyncManager.setPeriodicSync` re-enqueues the periodic work with UPDATE, or cancels it.
  - `ReleaseReminderWorker` / `WorkManagerReleaseReminderScheduler`: one-time work at 09:00 on the release
    day (`release_reminder_<id>`, REPLACE).
- **notifications**: `Notifier` → `SystemNotifier` (InboxStyle, at most 5 lines; channels: trending_movies,
  trending_series, articles, bestsellers (one notification per genre), release_reminders, games (new
  "coming soon" games), anime, manga (new in the top lists)).
  `DeepLinks`: `com.rick.ecs://trending_movie_catalog`, `…trending_series_catalog`, `…article_catalog`, `…game_catalog`,
  `…anime_catalog`, `…manga_catalog`, `…bestseller-catalog-screen?genre=`, `…trending_movie_details_fragment/<id>`.
- **translation**: `TextTranslator` (singleton) translates English text into the phone's language with
  Yandex Cloud Translate. It batches the texts of one request, keeps an LRU of 200 per language, returns
  `null` when English, offline or on an error. demo → `DemoTranslationNetworkDataSource` (assets
  `translation.json`), prod → `TranslationNetwork`. Key: `translationApiKey` in `local.properties` → BuildConfig.
  It replaced the per-feature `GetTranslationUseCase` / `TranslationViewModel`.
- **testing**: `EcsTestRunner` (swaps in `HiltTestApplication`), `MainDispatcherRule`, `TestNetworkMonitor`,
  `TestSyncManager`, `TestNotifier`, `TestAnalyticsHelper`, `TestUserSettingsDataRepository`. Exposes junit,
  kotlin-test, coroutines-test, turbine and hilt-testing as `api`.

## Trailers (`:data:trailer`)
- YouTube only, through android-youtube-player `core` (IFrame player in a WebView; YouTube allows no other
  player). TMDB, IGDB and Jikan all give YouTube ids; `youtubeIdFromUrl` reads Jikan's embed URLs.
- `MediaGallery(items, title)`: the details header pager. `GalleryItem.Trailer(id)` first (thumbnail, plays in
  place via `InlineTrailer`), then `GalleryItem.Image(url, wide)` (`wide` = 16:9, shown whole over a blurred
  copy). Page dots describe "Page n of m". XML screens use `MediaGalleryHost(frameLayout, onFirstImageLoaded)`
  (start the postponed enter transition there); movies/series build their items with `detailsGalleryItems`.
- `TrailerActivity.start(context, videoId, title, startSeconds)`: the full screen player (`EXTRA_*` are public).
  It handles rotation itself (`configChanges`), so the video doesn't reload. Every "Watch trailer" uses it.
- `YouTubePlayerHost` inflates `data_trailer_player.xml` (`enableAutomaticInitialization="false"`): a
  `YouTubePlayerView` built in code initializes itself before our options apply.
- Tests: `trailerLaunchesDuring {}` (app androidTest) blocks and records `TrailerActivity` launches with an
  `ActivityMonitor` (API 26+), so no test plays a video.

## Security helpers (`:data:common`)
- `security/KeystoreCipher(alias)`: `encrypt(String)` → Base64(IV + ciphertext + GCM tag), `decrypt` → null when the
  key is gone or the value was altered. No Tink (only the JVM build is cached, and it pulls full protobuf-java, which
  clashes with protobuf-javalite).

## Design system
- **XML (`:data:ui_design`)**: theme `DataUiDesignTheme.ECatalogs` (Material3 DayNight, NoActionBar),
  colour tokens in `values/colors.xml` (light) and `values-night/colors.xml` (dark, **the default look**).
  The token names are historical: `data_ui_design_black` is *primary text* (light-coloured in dark mode),
  `data_ui_design_white` is the background, `data_ui_design_color_buttons` is the terracotta accent
  (#A9533B light / #F0A48D dark). Poster text sits on scrims (`data_ui_design_scrim*`, `on_scrim`). Frosted
  glass: `glass_fill/sheen/rim`.
  - Type: High Tower serif (`@font/high_tower_text`) for headlines/titles, system sans for body. M2 attr
    names (`?attr/textAppearanceHeadline6`…) are mapped to M3 ones too.
  - Styles: `DataUiDesignFavoriteButton` (+ `.Accent`, `.Small`, `.Fill`), `DataUiDesignFavoriteWrapper`,
    `DataUiDesignCircleImage`, `DataUiDesignRoundedCorners`, poster dimens (`poster_card_height` 360dp…).
  - Motion: `motionNavOptions` / `NavController.navigateWithMotion(uri)` (Material shared-axis X),
    `navigateWithSharedElement(uri, view)` + `posterTransitionName(kind, id)` with
    `POSTER_KIND_MOVIE/SERIES/ANIME/MANGA`; `RecyclerView.useHeroCarousel()` (centered 72%-wide cards that
    shrink/fade at the sides; `View.fadeWhenSmall(views)`), `RecyclerView.animateItemsOnScroll()`.
- **Compose (`:data:ui_components:common`)**: `EcsTheme` (builds the M3 scheme from the same colour
  resources, `EcsTypography` with serif titles; **lint forbids `MaterialTheme {}` elsewhere**),
  `EcsHeroCarousel`, `EcsPosterRow`, `EcsChipRow`, `EcsSectionTitle`, `EcsPosterItem`/`EcsChip`,
  `EcsCatalogCard` / `EcsBookCatalogCard` (favorites cards with translatable summary through
  `LocalTextTranslator`), `EcsEmptyState`, `EcsText*`, `EcsSnackbar`, `RecentSearchesBody`.
  - Matching the XML screens from Compose: `EcsBackButton` (the toolbars' back arrow), `EcsGlassFavoriteButton`
    (white star on the glass circle, as on the XML cards), `EcsSearchResultCard` (the XML search result card).
  - View helpers: `TranslationToggle(button, targets, translator)` and `bind(texts…)` (cross-fades
    original ↔ translated text in XML screens), `provideGlide(imageView, url)`, `getTmdbImageUrl(path)`.
  - `installImageCaching(app)`: one 512 MB Glide disk cache; Coil loads http(s) through Glide
    (`GlideCacheFetcher`), so both libraries share the cache.
- **Favorites screens (Compose)**: `:data:ui_components:{movie,book,anime}_favorite` →
  `MovieFavScreen`, `BookFavScreen`, `AnimeFavScreen` (list + undo snackbar). Hosted in each feature's
  `*_favorite(s)` fragment through `ComposeView`.
- **Auth UI**: `:data:ui_components:auth` → `LoginScreen` (brand header + card, Sign in / Create account
  switch; test tags `TAG_*`), `PasswordResetDialog` (M3 `AlertDialog`).

## Settings (`:settings:*`)
- model (JVM): `SettingsUserData(userName, themeConfig = DARK, syncEnabled = true, syncIntervalHours =
  weekly, enabledNotifications = all)`, `ThemeConfig`, `NotificationCategory` (TRENDING_MOVIES,
  TRENDING_SERIES, ARTICLES, BESTSELLERS, GAMES), sync options: week (168 h, the default) or month (720 h).
- data: `UserSettingsDataRepository` → `UserSettingsDataRepositoryImpl` (backed by `PreferencesDataSource`;
  logs `username_changed`).
- screen: `SettingsFragment` hosts a Compose `SettingsScreen` (account: rename / sign out / delete account;
  notification switches, asking for POST_NOTIFICATIONS on 13+; background sync on/off + interval; theme;
  about: privacy policy, terms, OSS licenses, version). `SettingsViewModel` re-schedules the periodic sync
  when sync or its interval changes. Deep link `com.rick.ecs://settings`.

## Authentication (`:authentication:*`)
- `AuthenticationFragment` (start destination, deep link `com.rick.ecs://authentication_fragment`) hosts
  the Compose `LoginScreen`. Firebase Auth: email/password sign-in and sign-up, Google sign-in, One Tap
  (password + Google id token), password reset. An already signed-in user goes straight on.
- After sign-in it navigates to `com.rick.ecs://trending_series_catalog` (the "home" screen).
- Validation (`Util.kt`): password has no whitespace and at least 6 chars; username 3–12 chars, no whitespace.
- The username is saved through `UserSettingsDataRepository.setUserName`.
- `AuthSession` (`auth_data`, bound in `AuthModule` to `FirebaseAuthSession`) answers "signed in?", whether to
  offer One Tap, sign-out and account deletion. The login screen's start check and Settings use it, so app
  instrumented tests replace it (`TestAuthSession`). Signing in itself still calls Firebase directly.
- `auth_data` holds only `User(username)`.

## Feature layout (same in movie, book, anime, game)
```
<area>/data_<area>/model     JVM: domain models, User* models (model + isFavorite), *UserData (favorite id sets), Ui events/states
<area>/data_<area>/network   Retrofit APIs + DTOs in network/model; demo/prod flavor bindings; DemoXAssetManager reads src/main/assets JSON
<area>/data_<area>/database  Room DB, entities (+FTS, remote keys, recent searches), DAOs, migrations, Gson TypeConverters
<area>/data_<area>/data      repositories, RemoteMediators/PageWriters, Composite* (catalog × favorites), mappers asX()
<area>/data_<area>/domain    small use cases (GetXByIdUseCase combines details with favorites, recent-search use cases)
<area>/screen_<area>/*       one Fragment module per screen + its nav graph; common/ has shared views, WebViewFragment
```
- Screens are **XML/ViewBinding Fragments** with RecyclerView + Paging 3 adapters. New UI goes in **Compose
  through a `ComposeView`** inside these fragments; don't rewrite existing XML screens.
- Navigation between modules uses **deep links** (`findNavController().navigateWithMotion(Uri.parse("com.rick.ecs://…"))`),
  not Safe Args directions across modules. Each module's graph is `<include>`d by the app graph, or by
  the graph that opens it.
- Favorites live in DataStore (id sets), not Room. `Composite*Repository.observeX()` combines the paged
  catalog with the favorite ids into `User*` models; favorites screens query Room by those ids.
- Catalog caching pattern (movie and anime catalogs, Gutenberg):
  - Rows have a nullable `catalog_position = page * 1000 + index` (`CatalogPosition.kt`). The paged catalog is
    `WHERE catalog_position IS NOT NULL ORDER BY catalog_position`.
  - Search results / details are inserted with `insertOutsideCatalog` (IGNORE, position null), so they never
    replace a catalog row.
  - A refresh does `clearCatalog()` + `deleteOutsideCatalog(keepIds = favorites)` + clear remote keys, all in one
    transaction (`*TransactionRunner`). Favorites survive refreshes.
  - `RemoteMediator.initialize()` returns SKIP_INITIAL_REFRESH while `ListCache.isFresh(CachedList.X)`.
  - `syncFirstPage()` (used by `SyncWorker`) rewrites page 1 only when its ids changed, then returns the
    titles not seen in the previous sync (empty on the first run) for notifications.
- Search: network search → `insertOutsideCatalog` → Room `LIKE %q%` query (some areas also keep FTS
  tables). Recent queries are stored per catalog (`*RecentSearchQuery` tables, `RecentSearchesBody` UI).
- Translation in XML lists/details: `TranslationToggle(button, textView, textTranslator)` with a
  `TextTranslator` injected in the Fragment/ViewHolder.

## Movies & series (`:movie:*`), TMDB + NYT
- network: `TmdbNetworkDataSource` (`TmdbRetrofitNetwork`, base `https://api.themoviedb.org/3/`, `api_key`
  query parameter through `ApiKeyInterceptor`): trending movie/tv (week), search, movie/tv details with
  `append_to_response` (`similar,credits,videos,watch/providers,release_dates|content_ratings`), upcoming,
  now playing, airing today, discover by genre, genres, season, person (`combined_credits`).
  `ArticleNetworkDataSource` (`NyTimesRetrofitNetwork`, article search filtered to movie reviews,
  `api-key`). Demo: `DemoTmdbNetworkDataSource` / `DemoNyTimesNetworkDataSource` (assets JSON).
  Keys: `tmdbApiKey`, `nyTimesApiKey` in `local.properties`.
- database `MovieDatabase` **v2** (`MIGRATION_1_2` adds `catalog_position` to the trending movie, trending
  series and article tables). Tables: trending_movie / trending_series / article (+ `_fts_table`, remote keys,
  recent search queries).
- data:
  - `TrendingMovieRepository`, `TrendingSeriesRepository`, `ArticleRepository`: paging, search, `syncFirstPage()`.
    Articles start at page 0, TMDB at 1.
  - `*ByIdRepository`: details are not cached.
  - `PersonRepository`.
  - `TmdbBrowseRepository` (`@Singleton`):
    - upcoming (future dates only), now playing (by `Locale.country` region), airing today (fresh for the
      same day), movie/series genres (per language). Lists are cached as JSON files in `cacheDir/tmdb_browse`
      and gated by `ListCache`.
    - `discoverMovies/Series(genreId)` paging straight from the network.
    - `season()` (in-memory cache).
    - `findFilm(title, year)` (NYT review → TMDB movie) and `screenAdaptations(bookTitle)` (book → films and
      series), both matched with `normalizedTitle()`.
  - `ReleaseReminderRepository.toggleReminder(movie)` stores the reminder in DataStore and schedules it with
    `ReleaseReminderScheduler`.
  - Details extras (`TmdbDetailsExtras`): cast (max 15), best YouTube trailer, watch providers per country,
    age ratings.
- screens:
  - `trending_series_catalog`: **the home screen** (`com.rick.ecs://trending_series_catalog`).
  - `trending_movie_catalog`: `TrendingMovieViewModel` adds a genre filter (`selectGenre` switches to
    discover paging), plus upcoming (with reminder toggles) and now playing rows. The rows are shown by
    `BrowseHeaderAdapter` / `BrowseRowAdapter` (`movie/screen_movie/common/BrowseHeader.kt`, hero carousel)
    above the paged list.
  - `trending_movie_details` / `trending_series_details`: similar titles, extras (`DetailsExtras.bindExtras`:
    trailer, cast → person, providers). Series seasons are shown in a hero carousel; tapping one opens the
    `SeasonSheet` bottom sheet.
  - `person_details`: biography with translation, credits that open movie/series details.
  - `article_catalog`: NYT reviews. A review opens `ArticleDetailsFragment` (`:movie:screen_movie:common`, deep link
    `articleDetailsUri(id)` → `movie_common_article_details/{articleId}`, from catalog, search and favorites), in the
    bestseller details style: picture, source pill, headline, critic, date pill, "In short" (abstract), "From the
    review" (lead paragraph, translatable), "Read the full review" (in-app WebView), "The film" (`FilmLinkButton`,
    finds the film from the headline with `filmTitleFromReviewHeadline`). Star in the toolbar.
    `UserArticlesRepository.observeArticleById` reads any stored article.
  - `*_search` and `*_favorite` screens; favorites screens use Compose `MovieFavScreen`.
  - `common/WebViewFragment` (`com.rick.ecs://movie_common_webviewfragment/{link}`, URL-encoded).
- Deep links: `trending_movie_catalog`, `trending_series_catalog`, `article_catalog`,
  `trending_movie_details_fragment/{movie}`, `trending_series_details_fragment/{series}`,
  `person_details_fragment/{person}`, `movie_common_webviewfragment/{link}`.

## Books (`:book:*`): Gutendex + NYT Books
- network:
  - `GutenbergNetworkDataSource` (`GutenbergRetrofitNetwork`, `https://gutendex.com/books?page&languages&topic`,
    search `?search=`).
  - `BestsellerNetworkDataSource` (`BestsellerRetrofitNetwork`, NYT Books v3: `lists/{date}/{list}.json?offset`,
    `lists/overview.json`, `reviews.json?isbn`, plus article search for the review text; key `nyTimesApiKey`).
    `searchBestseller` is `TODO()` and **throws if called**; the `bestseller_search` screen is commented out.
- model: `BestsellerGenre` (FICTION, NONFICTION, MISCELLANEOUS, BUSINESS, GRAPHIC → NYT `listName`),
  `GutenbergFilter(language, topic)` with `cacheKey = "gutenberg:<lang|all>:<topic|all>"`.
- database `BookDatabase` **v3**: `MIGRATION_1_2` recreates `bestseller_table` (primary key `id + genre`),
  `MIGRATION_2_3` adds `gutenberg_table.catalog_position`. Also FTS, remote keys (`book_remote_keys`,
  `ny_book_remote_keys` with id `"<genre>|<isbn>"`) and recent searches.
- data:
  - Gutenberg: `GutenbergRemoteMediator` per filter. Books are sorted by downloads, positioned `page*1000+index`,
    and the list ends on an empty page. A refresh keeps favorites and marks only that filter fresh
    (`markRefreshedExclusive(..., prefix = "gutenberg")`).
  - Bestsellers: `BestsellerRemoteMediator` per genre (page size 20, offset paging). `syncAllGenres()` reads the
    overview first and refetches only the genres whose ranking changed (`listChanged`). It throttles to
    **5 NYT requests a minute** (13 s apart) and returns the new books per genre for notifications.
  - `BookReviewRepository.getReview(isbn)` → `BookReview` (NYT article when there is one, otherwise the listing).
- screens:
  - `bestseller_catalog`: `BestsellerFragment` with a genre chip list, a hero carousel and the paged list.
    Deep link `com.rick.ecs://bestseller-catalog-screen?genre={genre}` (used by the notifications).
  - `BestsellerDetailsFragment` (`com.rick.ecs://bestseller_details/{isbn}`): translatable description and
    review, plus `ScreenAdaptations` (Compose, from `TmdbBrowseRepository.screenAdaptations`, opens movie or
    series details).
  - `gutenberg_catalog`: `GutenbergHeaderAdapter` holds the language/topic filters; an empty state is shown in
    a `ComposeView`.
  - `gutenberg_search` (`SearchFragment`), favorites (Compose `BookFavScreen`).
  - `common/ReaderFragment` (`com.rick.ecs://gutenberg_reader/{book}?link={link}&title={title}`): a WebView
    reader that injects theme CSS. It keeps the text zoom and the reading progress per book in
    SharedPreferences `gutenberg_reader`.
  - `common/WebViewFragment` (`com.rick.ecs://book_common_webviewfragment/{link}`).
  - `ScreenAdaptationsSheet` (bottom sheet).

## Anime & manga (`:anime:*`): Jikan v4 (MyAnimeList)
- network: `JikanRetrofitNetwork` (`https://api.jikan.moe/`, no key). It is a **singleton with an app-wide
  throttle**: every request goes through `throttled {}` so requests start at least
  `MIN_REQUEST_INTERVAL_MILLIS` apart (Jikan allows about 3/s, 60/min). Keep new endpoints inside `throttled`.
  Top anime/manga use the popularity filter, and adult titles are excluded (`sfw`). Demo flavor:
  `DemoJikanNetworkDataSource`.
- database `JikanDatabase` **v2** (`MIGRATION_1_2` adds `catalog_position` to `anime_table` and `manga_table`),
  plus recent searches and FTS.
- data:
  - `AnimeRemoteMediator` / `MangaRemoteMediator` (start at page 1, end on an empty page, catalog pattern above).
  - Search returns at most 25 results.
  - `JikanBrowseRepository`:
    - season now/upcoming, featured manga rows (`MangaList`) and genres are cached as JSON files in
      `cacheDir/jikan_browse` + `ListCache`.
    - `browseAnime/Manga(list, genreId)` paging from the network (25 a page).
    - extras (broadcast time, streaming, trailer through `youtubeIdFromEmbedUrl`), episodes, recommendations
      (max 15), relations (adaptations first), characters with Japanese voice actors (max 25), person (roles,
      max 60) and the weekly `schedule(japanDay)` (≤ 3 pages, Asia/Tokyo) are cached **in memory for the
      session** (`remembered`).
    - `randomAnimeId/randomMangaId` (not cached).
- screens:
  - `anime_catalog`: `AnimeCatalogFragment`, with a Compose browse header (`JikanBrowseHeader` through
    `ComposeHeaderAdapter`) and a "random" button.
  - `AnimeScheduleFragment` (Compose, `com.rick.ecs://anime_schedule`): airing times converted with
    `Broadcast.inZone()`.
  - `anime_details` (`com.rick.ecs://anime_details_fragment/{anime}`): `AnimeExtrasSection` / `EpisodesSheet`
    (Compose), `JikanRelationsSection`, translatable synopsis + background.
  - `JikanPersonFragment` (Compose, `com.rick.ecs://jikan_person/{person}`).
  - `manga_catalog`, `manga_details` (`com.rick.ecs://manga_details_fragment/{manga}`,
    `MangaRecommendationsRow`), search and favorites (Compose `AnimeFavScreen`) for both.
  - `common/WebViewFragment` (`com.rick.ecs://anime_common_webviewfragment/{link}`).
  - The list RecyclerView id is `jikan_recycler_view` (used by the benchmarks).

## Video games (`:game:*`): IGDB (Twitch)
- No `domain` module: details are combined with favorites in `CompositeGameRepository.observeDetails`.
- Credentials: `igdbClientId` and `igdbClientSecret` in `local.properties` → `BuildConfig.IGDB_CLIENT_ID/SECRET`
  (`game/data_game/network/build.gradle.kts`). **The secret ships in the APK**: fine for development; move the
  token request to a backend before a public release.
- network:
  - `TwitchTokenProvider` gets the app access token (client-credentials, `https://id.twitch.tv/oauth2/token`),
    keeps it in SharedPreferences `igdb_auth` and renews it a day before it expires.
  - `IgdbAuthInterceptor` adds `Client-ID` + `Authorization: Bearer`; on a 401 it drops the token and retries once.
  - `IgdbRetrofitNetwork` (`https://api.igdb.com/v4/`, POST with an Apicalypse text body) is a singleton
    **throttled to one request every 260 ms** (IGDB allows 4/s).
  - Queries live in `IgdbQueries` (unit tested). Adult games are excluded (`themes != (42)`).
  - "Popular now" = `popularity_primitives` (type 1, IGDB visits) → `games where id = (…)`, reordered by popularity.
  - Demo flavor: `DemoIgdbNetworkDataSource` reads `assets/games.json` (no covers).
  - Images: `igdbImageUrl(imageId, IgdbImageSize)` (`images.igdb.com/.../t_cover_big/<id>.jpg`).
- database `GameDatabase` **v1** (`igdb-database`): `game_table` (with `catalog_position`), `game_remote_keys`,
  `game_recent_search_queries_table`. Lists are stored as JSON (`GameConverters`).
- data:
  - `GameRemoteMediator`: 10 pages of 50, the catalog pattern, `CachedList.GAMES`.
  - `GameRepository`: popular paging, favorites by id, search (network → `insertOutsideCatalog` → `LIKE`;
    offline searches the stored games), `gameDetails(id)` (not cached; stores the game so it can be a favorite).
  - `GameBrowseRepository`: coming soon (next 180 days by hype), recently released (last 60 days), genres
    (JSON files in `cacheDir/game_browse` + `ListCache`), `gamesByGenre` paging from the network.
  - `UserGameDataRepository` (favorites in DataStore proto field 25, `game_favorite_ids`), `CompositeGameRepository`,
    `GameRecentSearchRepository`.
- screens (all Compose; paged lists are a RecyclerView `PagingDataAdapter` with ComposeView rows,
  `GamePagingAdapter`, because `paging-compose` isn't in the Gradle cache):
  - `game_catalog` (drawer item / graph id `game_graph`, `com.rick.ecs://game_catalog`): Compose header with
    the coming-soon hero carousel, a recently-released row and genre chips. The list id is `game_recycler_view`.
  - `game_details` (`com.rick.ecs://game_details/{game}`): own top bar, trailer (YouTube), translatable summary
    and story, screenshots, platforms/companies, similar games, IGDB link.
  - `game_search` (`com.rick.ecs://game_search`, own top bar, recent searches), `game_favorites`
    (`com.rick.ecs://game_favorites`, undo snackbar).
  - `common`: `GameListItem`, `FavoriteButton`, `GameDeepLinks`, formatting (rating out of 10, release date).
- The Twitch token is stored encrypted (`KeystoreCipher` from `:data:common`, AES-256-GCM key in the Android Keystore,
  alias `igdb_access_token_key`); only its expiry is plain. Unreadable (restored on another device) → fetched again;
  a plaintext token from an older version is encrypted on first read.
- Background sync: page 1 of "popular now" is rewritten when it changed, and "coming soon" is downloaded
  again. Games that are new in "coming soon" since the previous sync are notified (category `GAMES`,
  Settings switch "Upcoming games"; proto fields 26 `notifications_disabled_games` and 27
  `notified_coming_soon_game_ids`). The first sync after install only records them.

## Tests
- Unit tests live next to the code (`src/test`), in JUnit4 + kotlin-test + coroutines-test + Turbine.
  Robolectric is used for the Room DAO and mediator tests (`src/test/resources/robolectric.properties`).
- Network fakes read JSON from `src/test/resources`. Each data module has a `testing/TestDoubles.kt`
  (`testListCache()` etc.).
- Instrumented tests use `EcsTestRunner`, which runs them on `HiltTestApplication`
  (`app/src/androidTest/.../EcsTestRunnerTest.kt`).
- App UI tests (`app/src/androidTest/.../ui`, demo flavor, need a device): `NavigationTest` (sign-in gate, drawer,
  Back), `LoginScreenTest`, `SettingsTest`, and one catalog-flow test per area (`ui/catalog`: catalog → details →
  Back keeps scroll → search → favorites; XML areas share `CatalogFlowTest`, games is `GamesCatalogTest`).
  - Hilt swaps: `TestAuthModule` (signed in, no One Tap), `TestDataStoreModule` (in-memory preferences per test).
    `useTestWorkManager()` keeps Settings' sync switch from starting real work.
  - Espresso **3.7.0** (3.6.1 crashes on API 36+: `InputManager.getInstance`), Compose ui-test (BOM alpha
    2025.09.01 → 1.10.0-alpha04), work-testing, recyclerview 1.2.1 (ConcatAdapter). espresso-contrib isn't used: the
    drawer is opened through `DrawerLayout` without animation (taps during the slide were lost), lists via `CatalogRobot`.
  - The AVDs (`Medium_Phone`, `Medium_Tablet`) are API 37. `KeystoreCipherTest` needs a real device/emulator.
- Lint wants androidTest names in `given_when_then` / `when_then` and no `test` prefix.
- Commands (**ask before running any Gradle build or test task**):
  - `./gradlew testDemoDebugUnitTest`, or one module, e.g. `:movie:data_movie:data:testDemoDebugUnitTest`.
  - `./gradlew :app:connectedDemoDebugAndroidTest` (needs a device).
  - Baseline profiles and benchmarks: see above.
- Coverage: debug builds record it (`enableUnitTestCoverage`/`enableAndroidTestCoverage`, as in NiA);
  `create{Variant}CombinedCoverageReport` (e.g. `createDemoDebugCombinedCoverageReport`) merges unit and
  instrumented runs.

## CI (GitHub Actions, `.github/workflows/build.yml`)
- Branches: `develop` (integration) and `main` (releases). Runs on pull requests into them, pushes to them,
  and by hand. A newer push to the same branch cancels the older run.
- Job **checks** (one Linux runner): `:build-logic:convention:check` → `:app:dependencyGuard` (against
  `app/dependencies/prodReleaseRuntimeClasspath.txt`) → unit tests (`testDemoDebugUnitTest` plus the JVM modules'
  `test`: `:lint`, anime/book/game `model`) → `:app:assemble` (all 4 variants, release shrunk by R8) →
  `:app:lintProdRelease :lint:lint` (the app checks its libraries too; `app/lint-baseline.xml`). Uploads APKs,
  test XML, lint HTML; lint SARIF goes to GitHub code scanning.
- Job **androidTest** (matrix API 26 and 34, x86_64 emulator with KVM): builds the demo debug and test APKs, then
  `connectedDemoDebugAndroidTest`; on 34 also the combined coverage report. Uploads the reports.
- No secrets needed (demo data, empty keys, debug-key signing, `skipMappingUpload=true` in
  `.github/ci-gradle.properties`, which CI copies to `~/.gradle`). Optional secret `GRADLE_ENCRYPTION_KEY`
  (`openssl rand -base64 16`) lets the Gradle action keep the configuration cache.
- The Gradle cache is written by `develop`/`main` builds and only read by pull requests.
- Not on CI: baseline profiles (the prod app needs a signed-in account to reach the catalog), macrobenchmarks.
- When a dependency changes: `./gradlew :app:dependencyGuardBaseline` and commit `app/dependencies/`.

## CD (`.github/workflows/release.yml`, "Release to Play")
- A tag `v<major>.<minor>[.<patch>]` on a commit in `main` builds `:app:bundleProdRelease` and uploads it to Play's
  **internal testing** track (`com.rick.moviecatalog`) with the R8 mapping and `distribution/whatsnew/`; then a
  GitHub release with the notes (no bundle: it contains the API keys). Production stays a manual promotion.
- Version from the tag: `v2.3.1` → versionName `2.3.1`, versionCode `20301` (`-PecsVersionCode`/`-PecsVersionName`
  in `app/build.gradle.kts`; without them local builds keep 10 / "2.2"). Minor and patch are 0–99.
- To release: edit `distribution/whatsnew/whatsnew-en-US` (≤ 500 characters) → merge into `main` →
  `git tag v2.3.0 && git push origin v2.3.0` → approve the run if the `play` environment asks → Play Console →
  Testing → Internal testing → promote. Dry run: Actions → Release to Play → Run workflow with a version.
- The job writes `local.properties` (signing + API keys) from the GitHub environment **`play`**'s secrets:
  `PLAY_SERVICE_ACCOUNT_JSON`, `UPLOAD_KEYSTORE_BASE64` (`base64 -i EcsKey.jks`), `UPLOAD_KEYSTORE_PASSWORD`,
  `UPLOAD_KEY_ALIAS`, `UPLOAD_KEY_PASSWORD`, `TMDB_API_KEY`, `NYT_API_KEY`, `TRANSLATION_API_KEY`, `IGDB_CLIENT_ID`,
  `IGDB_CLIENT_SECRET`. It fails if one is missing or the bundle came out debug-signed.
- Play access: a Google Cloud service account (JSON key) invited in Play Console → Users and permissions with
  "Release apps to testing tracks".
- The Crashlytics mapping upload is on here (CI's `skipMappingUpload` is dropped).

## Working rules for this repo
- Ask before running `./gradlew` build, compile or test tasks. The user builds, runs and commits themselves.
- Never read or copy API key values. Keys come from `local.properties` (`tmdbApiKey`, `nyTimesApiKey`,
  `translationApiKey`, `igdbClientId`, `igdbClientSecret`, `release*` signing values) into BuildConfig; just name the property the user must set.
- Metered connection: reuse dependency versions already in the Gradle cache (the ones Now in Android
  uses), and avoid new downloads or Gradle managed devices.
- New UI: Compose inside a `ComposeView` in the existing Fragment, themed with `EcsTheme`. Don't convert XML screens.
- For build or test infrastructure, copy the pattern from `../nowinandroid` and adapt it.
- New library modules get the resource prefix of their path, and every resource in them must use it.
- Add new Room columns with a migration and bump the version. Commit the schema JSON under `<module>/schemas`.
- Add new DataStore fields with the next proto number (28+), named so the default is the right behaviour.

## Worth checking (found while reading; not fixed)
- `movie/screen_movie/trending_series_details/.../movie_screen_movie_trending_series_details_nav_graph.xml`
  uses `android:id="@+id/movie_screen_movie_trending_movie_details_nav_graph"`, **the same id as the movie
  details graph**. Both are reachable through deep links, but the duplicate id could make id-based
  navigation go to the wrong graph.
- `movie_screen_movie_trending_series_favorite_nav_graph.xml` deep link `com.rick.ecs/trending_series_favorite_fragment`
  is missing `://`.
- `TranslationNetwork` sends `Authorization: ": Api-Key <key>"`. Yandex expects `Api-Key <key>`, so the
  leading `": "` looks wrong. Check it if prod translations fail.
- `BestsellerRetrofitNetwork.searchBestseller` is `TODO()` (it would crash if wired up).
- Back on a top-level screen doesn't go home: the drawer's `popUpTo(startDestination)` targets the auth screen,
  which `AuthenticationFragment` pops itself, so top-level screens stack up (Anime → Manga → Back = Anime).
- `SyncManager.requestSync()` is never called. If a "sync now" button uses it, it reuses the start-up
  work, which skips the sync when the last one is recent.
