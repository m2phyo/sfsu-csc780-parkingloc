<?php
/**
 * Description of ParkingLocDB
 *
 * @author Min
 * 
 * Main functions for ParkingLoc Database Connection
 */
class ParkingLocDB{
    //single instance of self shared among all instances
    private $db = null;
 
    // constructor
    function __construct() {
        require_once 'DB_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    // destructor
    function __destruct() {
 
    }

    // The clone and wakeup methods prevents external instantiation of copies of the Singleton class,
    // thus eliminating the possibility of duplicate objects.
    public function __clone() {
        trigger_error('Clone is not allowed.', E_USER_ERROR);
    }

    public function __wakeup() {
        trigger_error('Deserializing is not allowed.', E_USER_ERROR);
    }
    
    ////////////////////////////////////////////////////////////////////////
    //////////////////////////// User Model/////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    public function getUsers() {
        $users = $this->query("SELECT * FROM users");
        return $users;
    }

    public function getPageingUsers($offset, $pageSize) {
        $users = $this->query("SELECT * FROM users LIMIT " . $offset . "," . $pageSize);
        return $users;
    }

    /*
     * get the user information by user id
     * $id: the id of the user
     */

    public function getUserById($id) {
        $user = $this->query("SELECT * FROM users WHERE user_id = " . $user_id);
        return $user;
    }

    /*
     * get the user id by user name
     * $username: the username of the user
     */

    public function getUserIdByUsername($user_name) {
        $id = $this->query("SELECT id FROM users WHERE user_name = '" . $user_name . "';");
        return $id;
    }

    /*
     * add a user into database
     * $user_name: the user name
     * $password: the password used to login to website
     * $email: the user's email address
     */

    public function addUser($user_name, $password) {
        $user_name = $this->real_escape_string($user_name);
        $password = $this->real_escape_string($password);
        $sql = "INSERT INTO users(user_name, password) VALUES('" . $user_name . "', '" . $password . "')";
        $result = $this->query($sql);
        if($result != false){
            // get user details
            $uid = mysql_insert_id(); // last inserted id
            $result = mysql_query("SELECT * FROM users WHERE user_id = $uid");
            // return user details
            return mysql_fetch_array($result);
        }
        return false;
    }

    /*
     * delete a user from database
     * $id: the id of user
     */

    public function deleteUser($id) {
        $sql = "DELETE FROM users WHERE id = " . $id;
        $result = $this->query($sql);
        if($result != false){
            return true;
        }
        return false;
    }
      
    /*
     * update user home 
     * $user_name: the user_name that needed to be updated
     * $home_loc_id: new home_loc_id
     */

    public function updateUserHomeLocation($user_name, $home_loc_id) {
        $sql = "UPDATE users SET home_loc_id = '" . $home_loc_id . "' WHERE user_name = " . $user_name;
        $result = $this->query($sql);
        if($result != false){
            return true;
        }
        return false;
    }

    /*
     * verify if the user name and password matches
     * $user_name: the user name
     * $password: the related password
     */

    public function authenticateUser($user_name, $password) {
        $user_name = $this->real_escape_string($user_name);
        $password = $this->real_escape_string($password);
        $sql = "SELECT 1 FROM users WHERE user_name = '" . $user_name . "' AND  password = '" . $password . "'";
        $result = $this->query($sql);
        return $result->data_seek(0);
    }

    /*
     * verify if a user already exists in the database
     * $user_name: user name
     */

    public function doesUserNameExist($user_name) {
        $user_name = $this->real_escape_string($user_name);
        $sql = "SELECT * FROM users WHERE user_name = '" . $user_name . "'";
        $result = $this->query($sql);
        if (mysqli_num_rows($result) != 0) {
            return true;
        }
        return false;
    }
    
}

?>
