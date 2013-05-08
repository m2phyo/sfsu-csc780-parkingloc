<?php
/**
 * Get Location Information 
 * 
 * @author Min
 */
define("DB_HOST", "sfsuswe.com");
define("DB_USER", "mphyo");
define("DB_PASSWORD", "910622211");
define("DB_DATABASE", "student_mphyo");
// Array for JSON response
$response = array();

//Checking for post data
//if (isset($_POST['tag'])) {
    //getting task
     //$task = 'update';
    //$lat = 37.783133; $long = -122.4185093; $take = 1; $id = 2;
//    echo $latitude = doubleval($lat);
//    echo $longitude = doubleval($long);
    //echo $isTaken = intval($take);
//    echo $address = "";
//    echo $description = "";
    
    $task = $_POST['tag'];
    $id = $_POST['loc_id'];
    $name = $_POST['loc_name'];
    $latitude = $_POST['loc_latitude'];
    $longitude = $_POST['loc_longitude'];
    $isTaken = $_POST['loc_isTaken'];
    $address = $_POST['loc_address'];
    $description = $_POST['loc_description'];
    
    // DB connection
    $db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
    
 // check for tag type
 /**
  * Get Location Info data set from table.
 */
 if ($task == 'get') {
    // get locations from location_info table
    $result = mysqli_query($db, "SELECT * FROM location_info WHERE loc_isTaken = '$isTaken'");

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        // location id
        $response["location_info"] = array();
 
        while ($row = mysqli_fetch_array($result)) {
            // temp user array
            $location_info = array();
            $location_info["loc_id"] = $row["loc_id"];
            $location_info["loc_name"] = $row["loc_name"];
            $location_info["loc_latitude"] = $row["loc_latitude"];
            $location_info["loc_longitude"] = $row["loc_longitude"];
            $location_info["loc_description"] = $row["loc_description"];
            $location_info["loc_isTaken"] = $row["loc_isTaken"];
            $location_info["loc_address"] = $row["loc_address"];
 
            // push single location array into final response array
            array_push($response["location_info"], $location_info);
        }
        
        // success
        $response["success"] = 1;
        $response["message"] = "Retrieving Data Successful!!!";
 
        // echoing JSON response
        echo json_encode($response);
    } 
    else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "Retrieving Data Failed!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }
 }
 
  // check for tag type
 /**
  * Insert single data set into table.
 */
 if ($task == 'insert') {
    // get locations from location_info table
    $result = mysqli_query($db, "INSERT INTO location_info (loc_latitude, 
        loc_longitude, loc_isTaken, loc_address, loc_description) 
        VALUES('$latitude', '$longitude', '$isTaken', '$address', '$description')");

    // check for empty result
    if ($result) {
        
        // success
        $response["success"] = 1;
        $response["message"] = "Insert Location Data Successful!!!";
 
        // echoing JSON response
        echo json_encode($response);
    } 
    else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "Insert Location Data Failed!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }
 }
 
 // check for tag type
 /**
  * Check the location is taken or not.
 */
 if ($task == 'check') {
    // get locations from location_info table
    $result = mysqli_query($db, "SELECT * FROM location_info 
        WHERE loc_latitude = '$latitude' AND loc_longitude = '$longitude'");

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
 
        while ($row = mysqli_fetch_array($result)) {
            // get isTaken data
            $response["loc_isTaken"] = $row["loc_isTaken"];
 
        }       
        // success
        $response["success"] = 1;
        $response["message"] = "Retrieving isTaken Data Successful!!!";
 
        // echoing JSON response
        echo json_encode($response);
    } 
    else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "Retrieving isTaken Data Failed!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }
 }
 
 // check for tag type
 /**
  * Update the location is taken or not taken.
 */
 if ($task == 'update') {
    // update the location isTaken
    $result = mysqli_query($db, "UPDATE location_info SET loc_isTaken='$isTaken'
        WHERE loc_id='$id'");

    // check for empty result
    if ($result) {
     
        // success
        $response["success"] = 1;
        $response["message"] = "Updating isTaken Data Successful!!!";
 
        // echoing JSON response
        echo json_encode($response);
    } 
    else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "Updating isTaken Data Failed!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }
 }
   mysqli_close($db);
//}
?>
