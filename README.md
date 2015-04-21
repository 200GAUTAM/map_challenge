# Map Challenge
Mini-app to play around with the Google Maps API!

## Setup
### Git
Pull this repository into a folder on your local machine using the standard `git clone https://github.com/sureshs592/map_challenge.git` command.

This project makes use of a submodule (*the Volley library in the root folder*). Run `git submodule init` to initialize the Volley submodule.

Next, run `git submodule update` to pull the volley code from its respective repository.

### Android Studio
Import the project into Android Studio using the **Import Project...** option. There might be issues around the `buildToolsVersion` being used by the app and Volley library here.

Please change the `buildToolsVersion` in `app/build.gradle` and `volley/build.gradle` to the version installed on your machine.

### Maps v2 Android SDK
You can head over to the [Google Developer Console](https://console.developers.google.com) and create a project for getting the Maps v2 API key. Complete instructions (including debug key retrieval) over [here](https://developers.google.com/maps/documentation/android/start).

### Places API
In the same project created in the previous step, you can enable the Places API and generate a public Server key for using the Places API. You can place the generated key in the `Constants.java` file over [here](https://github.com/sureshs592/map_challenge/blob/master/app/src/main/java/com/suresh/mapchallenge/utils/Constants.java#L8).

## Libraries Used
1. Android Support Library v7 (appcompat + cardview)
2. Play Services (Maps + Location API)
3. Volley (networking + image loading)

## Known Issues
1. The black marker on the centre of the map (denoting the centre of the search) fails to show up occassionally.
