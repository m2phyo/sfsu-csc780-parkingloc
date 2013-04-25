<?php

/**
 * File to handle all API requests
 * Accepts GET and POST
 *
 * Each request will be identified by TAG
 * Response will be JSON data
 
 /**
 * check for POST request
 */
//if (isset($_POST['tag'])) {
if (isset($_POST['user_name']) && isset($_POST['password'])) {
    // get tag
    $tag = $_POST['tag'];
 
    // include db handler
    require_once 'include/ParkingLocDB.php';
    $db = new ParkingLocDB();
 
    // response Array
    $response = array("tag" => $tag, "success" => 0, "error" => 0);
 
    // check for tag type
    if ($tag == 'login') {
        // Request type is check Login
        $user_name = $_POST['user_name'];
        $password = $_POST['password'];
 
        // check for user
        $user = $db->authenticateUser($user_name, $password);
        if ($user != false) {
            // user found
            // echo json with success = 1
            $response["success"] = 1;
            $response["user_id"] = $user["user_id"];
            $response["user"]["user_name"] = $user["user_name"];
            $response["user"]["home_loc_id"] = $user["home_loc_id"];
            echo json_encode($response);
        } else {
            // user not found
            // echo json with error = 1
            $response["error"] = 1;
            $response["error_msg"] = "Incorrect user name or password!";
            echo json_encode($response);
        }
    } else if ($tag == 'register') {
        // Request type is Register new user
        $user_name = $_POST['user_name'];
        $password = $_POST['password'];
 
        // check if user is already existed
        if ($db->doesUserNameExist($user_name)) {
            // user is already existed - error response
            $response["error"] = 2;
            $response["error_msg"] = "User already existed";
            echo json_encode($response);
        } else {
            // store user
            $user = $db->addUser($user_name, $password);
            if ($user) {
                // user stored successfully
                $response["success"] = 1;
                $response["user_id"] = $user["user_id"];
                $response["user"]["user_name"] = $user["user_name"];
                $response["user"]["home_loc_id"] = $user["home_loc_id"];
                echo json_encode($response);
            } else {
                // user failed to store
                $response["error"] = 1;
                $response["error_msg"] = "Error occured in Registartion";
                echo json_encode($response);
            }
        }
    } else {
        echo "Invalid Request";
    }
} else {
    echo "Access Denied";
}
?>