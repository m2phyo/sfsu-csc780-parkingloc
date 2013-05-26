<?php
/**
 * Register service 
 * 
 * Registering new user with user name, password and home location.
 * 
 * @author Min
 * 
 * @param $name name of the register user
 * @param $password password of the register user
 * @param $home_address is the home address of the register user
 * @return Json Encode The User information and related message if user is found
 * in the database. If not just related message.
 */
define("DB_HOST", "sfsuswe.com");
define("DB_USER", "mphyo");
define("DB_PASSWORD", "910622211");
define("DB_DATABASE", "student_mphyo");

// $name = "test@test.com";
// $password = "asdf";
// $homeaddress = "1600 Holloway Ave, SF";

//Checking for post data
if (isset($_POST["user_name"]) && isset($_POST["password"]) && isset($_POST["home_address"])) {
    //getting task
    $name = $_POST["user_name"];
    $password = $_POST["password"];
    $homeaddress = $_POST["home_address"];

    /**
     * using geocoder function to get the latitude and longitude from the 
     * home address string
     */
    $loc = geocoder::getLocation($homeaddress);
    $lat = $loc['lat'];
    $lng = $loc['lng'];

    // DB Connection
    $db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
    
    //insert information into location_info data table
    $insertHomeAddress = mysqli_query($db, "INSERT INTO location_info (loc_name, loc_latitude, loc_longitude, loc_address) VALUES('Home', '$lat', '$lng', '$homeaddress')");
    // printf ("New Record has id %d.\n", mysqli_insert_id($db));
    $latestLocId = mysqli_insert_id($db);

    // insert a user into users table
    $result = mysqli_query($db, "INSERT INTO users (user_name, password, home_loc_id) VALUES('$name', '$password', '$latestLocId')");

    // check for insertion successful or not
    if($result && $insertHomeAddress){
        //if successfully registered
        $response["success"] = 1;
        $response["message"] = "Register Successful!!!";
    } 
    else {
        // if not successful
        $response["success"] = 0;
        $response["message"] = "Register Failed!!!";
    }
    // 
    echo json_encode($response);
    mysqli_close($db);


}

/**
 * Using good map service to get longitude and latitude
 * 
 * @param String $address The user's home address
 * @return $resp Longitude and Latitude information
 */
class geocoder{
    static private $url = "http://maps.google.com/maps/api/geocode/json?sensor=false&address=";

    static public function getLocation($address){
        $url = self::$url.urlencode($address);
        
        $resp_json = self::curl_file_get_contents($url);
        $resp = json_decode($resp_json, true);

        if($resp['status']='OK'){
            return $resp['results'][0]['geometry']['location'];
        }else{
            return false;
        }
    }

    static private function curl_file_get_contents($URL){
        $c = curl_init();
        curl_setopt($c, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($c, CURLOPT_URL, $URL);
        $contents = curl_exec($c);
        curl_close($c);

        if ($contents) return $contents;
            else return FALSE;
    }
}
?>
