<?php
/**
 * Get Location Information : perform varieties of tasks requested by the 
 * user on mobile device and response related information and data.
 * 
 * @author Min
 * @param string $task Type of tasks to perform.
 * @return Json Encode Object Location Information depend on user request.
 */
define("DB_HOST", "sfsuswe.com");
define("DB_USER", "mphyo");
define("DB_PASSWORD", "910622211");
define("DB_DATABASE", "student_mphyo");
// Array for JSON response
$response = array();
    //Declaring Post Data variables
    $take = 0;
    $insertIsTaken = intval($take);
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
    
 
 /**
  * Get Location Info data set from table for available parking spots which also
  * is not Home location.
  * 
  * @param String $isTaken the info to check the location is available or taken
  * @param type $name Description
 */
 // check for task
 if ($task == 'get') {
    // get locations from location_info table
    $result = mysqli_query($db, "SELECT * FROM location_info WHERE loc_isTaken = '$isTaken' AND loc_name != 'Home'");

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        // locations info
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
        // no query found
        $response["success"] = 0;
        $response["message"] = "Retrieving Data Failed!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }
 }
 

 /**
  * Insert single data set of available parking spot into table.
  * 
  * @param String $name Location Name
  * @param String $latitude Location Latitude
  * @param String $longitude Location Longitude
  * @param  Integer $insertIsTaken Location available information
  * @return JSON Encoded Object Response message for newly added location.
 */  
// check for task
 if ($task == 'insert') {
    // insert locations into location_info table
    $result = mysqli_query($db, "INSERT INTO location_info (loc_name, loc_latitude, 
        loc_longitude, loc_isTaken) 
        VALUES('$name', '$latitude', '$longitude', '$insertIsTaken')");

    // check for empty result
    if ($result) {
        
        // success
        $response["success"] = 1;
        $response["message"] = "Insert Location Data Successful!!!";
 
        // echoing JSON response
        echo json_encode($response);
    } 
    else {
        // not success
        $response["success"] = 0;
        $response["message"] = "Insert Location Data Failed!!!";
 
        // echo JSON
        echo json_encode($response);
    }
 }
 
 
 /**
  * Update the parking spot location is taken or available.
  * 
  * @param String $user_id User ID
  * @param String $loc_id Location ID
  * @return JSON Object Related information and message.
 */
 // check for tag type
 if ($task == 'check') {
     $user_id = $_POST['user_id'];
    // get parking spot information from location_info table
    $result = mysqli_query($db, "SELECT * FROM location_info 
        WHERE loc_id = '$id' AND loc_isTaken = '0'");

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        //update location isTaken
        $updateResult = mysqli_query($db, "UPDATE location_info SET loc_isTaken='1'
        WHERE loc_id='$id'");
        //update user's reserve parking spot information.
        $updateUserReserve = mysqli_query($db, "UPDATE users SET reserved_id = '$id'
                WHERE user_id = '$user_id'");
        
        if($updateResult && $updateUserReserve){
            // success
            $response["success"] = 1;
            $response["message"] = "Reserve Successful!!!";
 
            // echoing JSON response
            echo json_encode($response);
        }
        else {
            // not success
            $response["success"] = 0;
            $response["message"] = "Update Error!!!";

            // echo no users JSON
            echo json_encode($response);
        }
    } 
    else {
        // if the spot is taken.
        $response["success"] = 0;
        $response["message"] = "Sorry The Spot is Taken!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }
 }
  
 /**
  * Update the location is taken or not taken.
  * 
  * @param String $user_id User ID
  * @param String $loc_id Location ID
  * @return JSON Object Related information and message.
 */
// check for tag type
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
  
 /**
  * Release the reserved location back to available.
  * 
  * @param String $user_id User ID to look for related user's reserved location ID
  * @return JSON Object Related information and message.
 */
// check for tag type
 if ($task == 'release') {
     $user_id = $_POST['user_id'];
     
     //Check user has reserved location.
    $checkIsTaken = mysqli_query($db, "SELECT reserved_id FROM users 
        WHERE user_id = '$user_id'");
    $row = mysqli_fetch_array($checkIsTaken);
    
    if(is_null($row["reserved_id"])){
        // no reserved location
        $response["success"] = 0;
        $response["message"] = "No Reserved Spot to Release!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }else{
        // reserved location
        $reserve_loc_id =  $row["reserved_id"];
        // update the location isTaken back to '0' which means available.
        $result = mysqli_query($db, "UPDATE location_info SET loc_isTaken = '0'
            WHERE loc_id = '$reserve_loc_id'");
        //update location info to null which means user has no reserved spot.
        $resultUser = mysqli_query($db, "UPDATE users SET reserved_id = NULL
            WHERE user_id='$user_id'");

        // check for empty result
        if ($result && $resultUser) {

            // success
            $response["success"] = 1;
            $response["message"] = "Updating Release Successful!!!";

            // echoing JSON response
            echo json_encode($response);
        } 
        else {
            // not success
            $response["success"] = 0;
            $response["message"] = "Updating Release Failed!!!";

            // echo no users JSON
            echo json_encode($response);
        }
    }
 }
  
 /**
  * Check for user's reserved location info and response with location ID and
  * message if there is one. If not, response with message.
  * 
  * @param String $user_id Current log in ser ID
  * @return JSON Object Message and information related to request.
  * 
 */
// check for tag type
 if ($task == 'getReserved') {
     $user_id = $_POST['user_id'];
    // get user's reserved location ID.
    $result = mysqli_query($db, "SELECT * FROM users WHERE user_id = '$user_id'");

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        $response["user"] = array();
 
        while ($row = mysqli_fetch_array($result)) {
            // temp user array
            $user = array();
            $user["reserved_id"] = $row["reserved_id"];

            // push single user array into final response array
            array_push($response["user"], $user);
        }
    
        if ($user["reserved_id"] != null){
            // success
            $response["success"] = 1;
            $response["message"] = "You have a reserved spot: " . $user["reserved_id"];

            // echoing JSON response
            echo json_encode($response);
        } 
        else {
            // no reservation found
            $response["success"] = 0;
            $response["message"] = "No Reserved Spot!!!";

            // echo no users JSON
            echo json_encode($response);
        }
    }else {
        // no reservation found
        $response["success"] = 0;
        $response["message"] = "No Reserved Spot!!!";

       // echo no users JSON
       echo json_encode($response);
     }
 }
   mysqli_close($db);
?>
