## Week 1 ##

  * Study the Google Map API V2.
  * Try to make Google Map to work.

## Week 2 ##

  * **Truong**
    1. Google Maps works successfully.
    1. Add some marker to the map.

  * **Min Min**
    1. Study Google App Engine.
    1. Testing connection to network using Google App Engine.

To do next week:
  1. Demo of Google App Engine.
  1. Login screen.
  1. Design database.
  1. Get user current location & add button for Home location.
  1. Get GPS info from file over the Internet.

## Week 3 ##

  * **Min Min**
    1. Set up App Engine Account for Application.
    1. Set up Web Application.
    1. Set up Google Cloud SQL for database and created tables for data.
    1. Still working on communication between the Application and backends.

  * **Truong**
    1. Login Page.
    1. Still working on getting current location and put the marker on map.

To do next 2 weeks:
  1. Registration, Login, Profile, Update
  1. Show available parking spot within the radius of home location.
  1. Tab on location and make reservation.
  1. Make intent to get navigation to tab location from current location.
  1. Working on the UI
  1. Show available parking spots
  1. Login screen
  1. Register screen
  1. Profile screen    # updating profile, password
  1. Update location spots
  1. Base on home location, querry parking spots around half mile
  1. Tap on spot, reserver the spot    # marked USED in db intent to navigate, drive to the spot

## Week 4 ##

  1. Set up MySQL Database.
  1. Deployed PHP API for database access through web.
  1. Implemented Java Class in Application for database.
  1. Still working on communication between the Application and backends.


## Week 5 ##

  * **Min Min**
    1. Designed and Created Database Tables.
      * User table with user ID, user\_name, password and home location.
      * Location Information table to store Location Address and Description.
    1. Deployed PHP Web Server for MySQL Database Access.
      * login.php - check and get PHP _POST user information tags and query from the MySQL database. Then reply success key and message in JSON format.
      * register.php - check and get PHP_POST user information tags and Insert into Database table. Reply success key and message in JSON format.
    1. Implemented the JSONParser Class to communicate and get data from Server.
      * This class connect with PHP web server using HTTP Request.
      * Using POST method to send information to web.
      * Getting HTTP response entity back and parse the data into JSON Object.
    1. Implemented UserFunctions Class to access data.
      * Initial implementation of some User Functions for getting location information and updating etc.

  * **Truong**
    1. Login Activity.
    1. Register Activity.
    1. Check for correct email and password before login.
    1. Set up tabs for Map Layout.
    1. Get current location working.
    1. Able to add markers on the map.

## Week 6 ##
  * **Truong**
    1. UI: Place marker on the map and store that marker to online database.
    1. UI: Refresh button will load locations from database and put them as markers on the current map view.
    1. UI: Mark the spot is taken (reserve) and refresh the map, the spot will disappear.
    1. UI: Marker will only show up in current screen view instead of show up everywhere outside screen border to save phone memory. When user move to a different screen view, refresh button will bring up those available spots on that view.
    1. UI: User is able to navigate from current location to the selected spot using the built-in Maps app or Navigation.

  * **Min Min**
    1. Implemented and Deployed getLocation.php to get live data
      * get - to get the locations of available parking spots
      * update - Update the location is taken or not taken
      * insert - insert the new location info.
      * check - check the parking spot is available or taken
    1. Updated register.php
      * Insert new user info with home location.

## Week 7 ##
  * **Truong**
    1. Use Geocoder to convert user home address to longitude and latitude.
    1. Map is now display the correct Home marker of the user.

  * **Min Min**
    1. Updated login.php
      * query to get the home location info related to current login user.
    1. Deployed chagePassword.php
      * To handle the change password activity for current user and update database.
    1. ChangePassActivity.java
      * Activity to get user new password and communicate with server.

## Week 8 ##
  1. Reserved Tab:
    * Show spot that is reserved by current user.
    * User will be able to release the spot.
    * Map will show up the spot again.
    * Some messages will be displayed if user has no reserved spot.