<?php
 
/*
 * Following code will get single bprofile details
 * A bprofile is identified by bprofile id (band_name)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["user_name"])) {
    $user_name = $_GET['user_name'];
 
    // get a product from products table
    $result = mysql_query("SELECT band FROM `band_members` WHERE `user_accepted` ='$user_name'");  
   
 
    
        if (mysql_num_rows($result) > 0) {
	
 		$response["bands"] = array();

            while ($row = mysql_fetch_array($result)) {
		
		array_push($response["bands"], $row);
		}
 
            // success
            $response["success"] = 1;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no bprofile found
            $response["success"] = 0;
            $response["message"] = "No bprofile found, Error 1";
 
            // echo no users JSON
            echo json_encode($response);
        }
   
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>