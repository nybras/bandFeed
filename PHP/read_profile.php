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
if (isset($_GET["band_name"])) {
    $band_name = $_GET['band_name'];
 
    // get a product from products table
    $result = mysql_query("SELECT * FROM `band_profile` WHERE `band_name` = '$band_name'");

	$result2 = mysql_query("SELECT name, role FROM `band_members` WHERE `band` = '$band_name'") or die(mysql_error());
 
// check for empty result
if (mysql_num_rows($result2) > 0) {
    // looping through all results
	// bmembers node
    	$response["bmembers"] = array();
	$bmembers = array();
	 $i = 0;
    while ($row = mysql_fetch_array($result2)) {
        // temp user array
        
		
            $bmembers["name$i"] = $row["name"];
            $bmembers["role$i"] = $row["role"];
		$i++;
            
	
	 }
	// push single bprofile into final response array
        array_push($response["bmembers"], $bmembers);
}
   
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $bprofile = array();
            $bprofile["band_name"] = $result["band_name"];
            $bprofile["genre1"] = $result["genre1"];
            $bprofile["genre2"] = $result["genre2"];
            $bprofile["genre3"] = $result["genre3"];
			$bprofile["county"] = $result["county"];
			$bprofile["town"] = $result["town"];
			$bprofile["amountOfMembers"] = $result["amountOfMembers"];
			$bprofile["soundCloud"] = $result["soundCloud"];
			$bprofile["webpage"] = $result["webpage"];
			$bprofile["image"] = $result["image"];
            $bprofile["created_at"] = $result["created_at"];
            $bprofile["updated_at"] = $result["updated_at"];
		$bprofile["followers"] = $result["followers"];
		$bprofile["bio"] = $result["bio"];
		
				
            // success
            $response["success"] = 1;
 
            // user node
            $response["bprofile"] = array();
 
            array_push($response["bprofile"], $bprofile);
 
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
        // no bprofile found
        $response["success"] = 0;
        $response["message"] = "No bprofile found, Error 2";
 
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