<?php
/**
 * Login service 
 * 
 * @author Min
 */

// Array for JSON response
$response = array();

// Include DB connect class
require_once 'include/DB_connect.php';
$db = new DB_connect();

//Checking for post data
if (isset($_POST['user_name']) && isset($_POST['password'])) {
    //getting task
//    $name = $_POST['user_name'];
//    $password = $_POST['password'];
    
//    $sql = "SELECT 1 FROM users WHERE user_name = $name AND password = $password";
//    $result = mysql_query($sql);
    
    // get a user from users table
    $result = mysql_query("SELECT 1 FROM users WHERE user_name = '". $_POST["user_name"]."' AND password = '". $_POST["password"]."'");
    
    $item = mysql_fetch_row($result);
    
    // check for empty result
    if (mysql_num_rows($result) > 0) {
        // looping through all results
        // user node
        $response["user"] = array();
 
        while ($row = mysql_fetch_array($result)) {
            // temp user array
            $user = array();
            $user["user_id"] = $row["user_id"];
            $user["user_name"] = $row["user_name"];
            $user["password"] = $row["password"];
            $user["home_loc_id"] = $row["home_loc_id"];
 
            // push single user into final response array
            array_push($response["user"], $user);
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
    
}
?>
