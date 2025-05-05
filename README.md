# MovieFinder

A **Compose Multiplatform** application that fetches and displays movies and TV shows from TMDB API, using modern Android and iOS architectures.

![firtst_image](https://github.com/user-attachments/assets/dba0afff-672a-44d1-b728-a221608a74bc)
![second_image](https://github.com/user-attachments/assets/2427e47f-54ed-4b49-b43c-83906b3058c1)


## 📦 Libraries & Technologies

* **Ktor**: HTTP client for making network requests to TMDB API.
* **TMDB API**: Provides movie and TV data (listings, details, videos, images, credits).
* **Paging 3**: Handles pagination of large data sets, ensuring smooth scrolling and efficient memory usage.
* **Room**: Local database to store favorite movies and TV shows using SQLite with strong type safety.
* **Coroutines**: Asynchronous programming for non-blocking I/O and concurrency.
* **Koin**: Dependency Injection framework for easy and modular DI setup.
* **Coil**: Image loading library for Compose to fetch and display posters, backdrops, and thumbnails.
* **Jetpack Compose Multiplatform**: UI toolkit for building declarative UIs on Android and iOS.
* **MVVM & Clean Architecture**: Separates concerns into presentation, domain, and data layers for maintainability and testability.

## 🚀 Features

* Browse popular, top-rated, now-playing, upcoming, trending (daily & weekly), and free-to-watch movies/TV shows.
* Search and discover with filters (genre, year, content type, sort order).
* Paging support for infinite scrolling.
* Detail screens with images, videos, and cast lists.
* Favorites stored locally via Room.
* Language & region localization support.

## ⚙️ Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/MuhammetKonukcu/MovieFinder.git
   ```
2. Add your TMDB API token to `local.properties` in the project root:

   ```properties
   tmdb_auth_token=YOUR_TMDB_API_TOKEN_HERE
   ```
3. Open the project in IntelliJ/Android Studio or Xcode (via Gradle sync).
4. Build and run on Android or iOS target.

---
