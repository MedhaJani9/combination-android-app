Combination App is a Java-based Android application built using Android Studio, Firebase Authentication, Cloud Firestore, OkHttp, and Picasso.
It integrates Forums, Tasks, and Weather modules into a single app using Fragments and RecyclerView with real-time Firestore updates.
The app supports user authentication, CRUD operations, image uploads, API-based weather data parsing (JSONObjects & JSONArrays), and asynchronous networking.
Designed to demonstrate mobile app architecture, backend integration, and API consumption in a scalable, exam-ready project.



Quick implementation notes:

All networking uses OkHttp. I used enqueue() (async) for production-like behavior. The commented sync example is for exam reference (you must run sync calls off the UI thread).

JSON object parsing example is in WeatherFragment (JSONObject main = root.optJSONObject("main")).

JSON array parsing example is in ForecastFragment (iterating list JSONArray).

Picasso loads icons via the OpenWeather icon URL: https://openweathermap.org/img/wn/{icon}@2x.png.

Use OPENWEATHER_API_KEY (place it in both WeatherFragment and ForecastFragment if you want real API data). If left null, the fragments fall back to the sample endpoints you were previously told to use so you can test immediately.

All AlertDialogs are inline and used for validation & network/parse errors.

ViewBinding is used for convenience — ensure it is enabled in your build.gradle (module).
