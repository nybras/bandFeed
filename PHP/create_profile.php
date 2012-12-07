<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
$names = array();
$roles = array();

// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['band_name']) && isset($_POST['genre1']) && isset($_POST['genre2']) && isset($_POST['genre3']) && isset($_POST['county']) && isset($_POST['town']) && isset($_POST['amountOfMembers']) && isset($_POST['soundc_link']) && isset($_POST['image']) && isset($_POST['bio'])) {
 
    $band_name = $_POST['band_name'];
    $genre1 = $_POST['genre1'];
    $genre2 = $_POST['genre2'];
	$genre3 = $_POST['genre3'];
	$county = $_POST['county'];
	$town = $_POST['town'];
	$amountOfMembers = $_POST['amountOfMembers'];
	$soundc_link = $_POST['soundc_link'];
	$image = $_POST['image'];
	$bio = $_POST['bio'];

	
	// put the band members and their roles into PHP arrays
	for ($i=0; $i<$amountOfMembers; $i++)
  	{
  		$names[$i] = $_POST["name$i"];
		$roles[$i] = $_POST["role$i"];
  	}
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new band profile
    $result = mysql_query("INSERT INTO bprofile(band_name, genre1, genre2, genre3, county, town, amountOfMembers, soundc_link, image, bio) VALUES('$band_name', '$genre1', '$genre2', '$genre3', '$county', '$town', '$amountOfMembers', '$soundc_link', '$image', '$bio')");
	
	// mysql inserting new members
	$results = array();
	for ($j=0; $j<$amountOfMembers; $j++)
  	{
  		$results[$j] = mysql_query("INSERT INTO bmembers(band, name, role) VALUES('$band_name', '$names[$j]', '$roles[$j]')");
  	}

	if (count($results) == $amountOfMembers) {
		$result = True;
	} else {
		$result = False;
	}
	 

 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Profile successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
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