# E-Catalog
A comprehensive catalog of entertainment topics, including television, books, movies, anime, and manga.

The project presents catalogs of:
* **Movies**: Recent movie releases and details.
* **TV Series**: Top 150 IMDB series.
* **Books**: Most downloaded books from the Gutenberg project and New York Times Bestsellers (Hardcover Fiction and Miscellaneous).
* **Anime & Manga**: Data from MyAnimeList via the Jikan API.

Features include:
* **Search**: Universal search screen for all entertainment topics.
* **Favorites**: Save your favorite movies, books, and animations in one place.
* **Authentication**: Sign-in and account creation to manage your personal catalogs.

## Tech Stack
* **Architecture**: Clean Architecture with MVI/MVVM.
* **Data**: Room Database, Retrofit for Networking, Paging 3 for pagination.
* **UI**: Jetpack Compose and XML/ViewBinding.
* **Security**: API keys are secured using NDK/JNI.

## Getting Started
To run the project, you need to obtain your own API keys:
1. **NYT API**: Get a free key from [developer.nytimes.com](https://developer.nytimes.com/apis) for movie articles and bestseller data (enable the Article Search and Books APIs).
2. **TMDB API**: Get a key from [themoviedb.org](https://www.themoviedb.org/settings/api) for trending movies and series.

### Adding API Keys
Add the keys to `local.properties` in the project root (this file is git-ignored):
```properties
tmdbApiKey=YOUR_TMDB_KEY
nyTimesApiKey=YOUR_NYT_KEY
```
They are exposed as `BuildConfig` fields in the network modules (`:movie:data_movie:network`, `:book:data_book:network`) and added to requests there, so no other code handles keys.

## Project Structure
The project is modularized by feature:
* `:app`: Main entry point.
* `:movie`: Movie and TV series features.
* `:book`: Gutenberg and NYT Bestseller features.
* `:anime`: Anime and Manga features.
* `:data`: Shared data modules (UI components, design system, and auth).

## Screenshots
![movies](https://user-images.githubusercontent.com/72414394/196361471-b9792801-538a-4dc3-8b26-594a65700eb4.jpg)
![series](https://user-images.githubusercontent.com/72414394/196361540-6284ff87-19ad-450b-bb65-fa45ab31534c.jpg)
![books](https://user-images.githubusercontent.com/72414394/196361561-6b6faab0-d561-4e39-9e72-caf7d683bc23.jpg)
![manga](https://user-images.githubusercontent.com/72414394/196361594-7cde645e-d57f-4c2c-afc6-a5995280e71d.jpg)
![anime](https://user-images.githubusercontent.com/72414394/196361607-d7abfb6d-d50f-4863-928c-4ef84d72d475.jpg)
![search](https://user-images.githubusercontent.com/72414394/196361636-01454c3d-95f1-4c96-affa-ac8975db6478.jpg)
![nav drawer](https://user-images.githubusercontent.com/72414394/196361646-53710978-fabb-4b8a-938f-560a05866f60.jpg)
