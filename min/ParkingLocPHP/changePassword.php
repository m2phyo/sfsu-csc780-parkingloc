<?php
/**
 * Change Password service 
 * 
 * @author Min
 */
define("DB_HOST", "sfsuswe.com");
define("DB_USER", "mphyo");
define("DB_PASSWORD", "910622211");
define("DB_DATABASE", "student_mphyo");

//Checking for post data
if (isset($_POST["user_name"]) && isset($_POST["old_password"])) {
    //getting task
    $name = $_POST["user_name"];
    $password = $_POST["old_password"];
    $newPassword = $_POST["new_password"];
    $task = $_POST["tag"];
    
    //DB Connection
    $db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
    
 if ($task == 'changePassword') {
    // update the location isTaken
    //$result = 
     mysqli_query($db, "UPDATE users SET password='$newPassword'
        WHERE user_name='$name' AND password='$password'");

    // check for empty result
    if (mysqli_affected_rows($db) > 0) {
     
        // success
        $response["success"] = 1;
        $response["message"] = "Update Password Successful!!!";
 
        // echoing JSON response
        echo json_encode($response);
    } 
    else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "Update Password Failed!!!";
 
        // echo no users JSON
        echo json_encode($response);
    }
 }
}
?>
