<?php
/**
 * Change Password service : Change user's password information. 
 * 
 * @author Min
 * 
 * @param String $name Current log in username
 * @param String $password User's current password
 * @param String $newpassword User's new password
 * @param type $name Description
 * @return JSON Object Response Message
 */
define("DB_HOST", "sfsuswe.com");
define("DB_USER", "mphyo");
define("DB_PASSWORD", "910622211");
define("DB_DATABASE", "student_mphyo");

//Checking for post data
if (isset($_POST["user_name"]) && isset($_POST["old_password"])) {
    
    $name = $_POST["user_name"];
    $password = $_POST["old_password"];
    $newPassword = $_POST["new_password"];
    $task = $_POST["tag"];
    
    //DB Connection
    $db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
    //
    if ($task == 'changePassword') {
        // update user's password
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
            // not success
            $response["success"] = 0;
            $response["message"] = "Update Password Failed!!!";

            // echo no users JSON
            echo json_encode($response);
        }
     }
}
?>
