<?php
 
/*
 * Following code will list all the bprofile
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// get all bprofile from bprofile table
$result = mysql_query("SELECT *FROM bprofile") or die(mysql_error());
 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // bprofile node
    $response["bprofile"] = array();
 
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $bprofile = array();

 /// THIS IS FUCKED

            $bprofile["band_name"] = $result["band_name"];
            $bprofile["genre1"] = $result["genre1"];
            $bprofile["genre2"] = $result["genre2"];
            $bprofile["genre3"] = $result["genre3"];
			$bprofile["county"] = $result["county"];
			$bprofile["town"] = $result["town"];
			$bprofile["members"] = $result["members"];
			$bprofile["soundc_link"] = $result["soundc_link"];
			$bprofile["pic_link"] = $result["pic_link"];
            $bprofile["created_at"] = $result["created_at"];
            $bprofile["updated_at"] = $result["updated_at"];
 
        // push single bprofile into final response array
        array_push($response["bprofile"], $bprofile);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no bprofile found
    $response["success"] = 0;
    $response["message"] = "No bprofile found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>