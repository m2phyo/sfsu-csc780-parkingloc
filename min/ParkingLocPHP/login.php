<?php
/**
 * Login service 
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
if (isset($_POST['user_name']) && isset($_POST['password'])) {
    //getting task
    $name = $_POST['user_name'];
    $password = $_POST['password'];
    
    // DB connection
    $db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
    
    // get a user from users table
    //$result = mysqli_query("SELECT * FROM users WHERE user_name = '". $_POST["user_name"]."' AND password = '". $_POST["password"]."'");
    $result = mysqli_query($db, "SELECT user_id, user_name, password, loc_latitude, loc_longitude, loc_address
        FROM users JOIN location_info WHERE users.home_loc_id = loc_id AND user_name = '$name' AND password = '$password'");

    // check for empty result
    if (mysqli_num_rows($result) > 0) {
        // looping through all results
        // user node
        $response["user"] = array();
 
        while ($row = mysqli_fetch_array($result)) {
            // temp user array
            $location_info = array();
            $location_info["user_id"] = $row["user_id"];
            $location_info["user_name"] = $row["user_name"];
            $location_info["password"] = $row["password"];
            $location_info["home_loc_id"] = $row["home_loc_id"];
            $location_info["home_loc_lat"] = $row["loc_latitude"];
            $location_info["home_loc_lng"] = $row["loc_longitude"];
            $location_info["home_loc_add"] = $row["loc_address"];
            
            // push single user into final response array
            array_push($response["user"], $location_info);
        }
        
        // success
        $response["success"] = 1;
        $response["message"] = "Login Successful!!!";
 
        // echoing JSON response
        echo json_encode($response);
    } 
    else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "User Name or Password Incorrect!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }
    
   mysqli_close($db);
}
?>
