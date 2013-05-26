<?php
/**
 * Register service 
 * 
 * @author Min
 */
define("DB_HOST", "sfsuswe.com");
define("DB_USER", "mphyo");
define("DB_PASSWORD", "910622211");
define("DB_DATABASE", "student_mphyo");

//Checking for post data
if (isset($_POST["user_name"]) && isset($_POST["password"])) {
    //getting task
    $name = $_POST["user_name"];
    $password = $_POST["password"];
    
    //DB Connection
    $db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
    
    // insert a user into users table
    $result = mysqli_query($db, "INSERT INTO users (user_name, password) VALUES('$name', '$password')");
    // check for empty result
    if($result){          
        $response["success"] = 1;
        $response["message"] = "Register Successful!!!";
 
        // echoing JSON response
        echo json_encode($response);
        } 
    else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "Register Failed!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }
    mysqli_close($db);
}
?>
