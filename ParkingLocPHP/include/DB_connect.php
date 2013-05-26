<?php
/**
 * Database Connection handler class
 *
 * @author Min
 */
class DB_connect {
        // constructor
    function __construct() {
        //conneting to database
        $this->connect();
    }
 
    // destructor
    function __destruct() {
        //closing database connection
        $this->close();
    }
 
    // Connecting to database
    public function connect() {
        require_once 'config.php';
        // connecting to mysql
        $con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
 
        // return database handler
        return $con;
    }
 
    // Closing database connection
    public function close() {
        mysqli_close();
    }
}

?>
