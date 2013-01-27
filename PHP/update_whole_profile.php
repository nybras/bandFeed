<?php
 
/*
 * Following code will update a product information
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['band_name']) && isset($_POST['genre1']) && isset($_POST['genre2']) && isset($_POST['genre3']) && isset($_POST['county']) && isset($_POST['town']) && isset($_POST['members']) && isset($_POST['soundc_link']) && isset($_POST['pic_link'])) {
 
    $band_name = $_POST['band_name'];
    $genre1 = $_POST['genre1'];
    $genre2 = $_POST['genre2'];
	$genre3 = $_POST['genre3'];
	$county = $_POST['county'];
	$town = $_POST['town'];
	$members = $_POST['members'];
	$soundc_link = $_POST['soundc_link'];
	$pic_link = $_POST['pic_link'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched band_name
	$result = mysql_query("UPDATE bprofile SET band_name = '$band_name', genre1 = '$genre1', genre2 = '$genre2', genre3 = '$genre3', county = '$county', town = '$town', members = '$members', soundc_link = '$soundc_link', pic_link = '$pic_link' WHERE band_name = $band_name");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Product successfully updated.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
 
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>
