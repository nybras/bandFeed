<?php
 
/*
 * Following code will update a product information
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
$result = NULL;

    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
	
 
// check for required fields
if (isset($_POST['band_name'])) {

	$band_name = $_POST['band_name'];


	if (isset($_POST['genre1']) && isset($_POST['genre2']) && isset($_POST['genre3'])) {

		$genre1 = $_POST['genre1'];
		$genre2 = $_POST['genre2'];
		$genre3 = $_POST['genre3'];
		$result = mysql_query("UPDATE band_profile SET genre1 = '$genre1', genre2 = '$genre2', genre3 = '$genre3', updated_at=now() WHERE band_name = '$band_name'");
	}

	elseif (isset($_POST['county']) && isset($_POST['town'])) {

		$county = $_POST['county'];
		$town = $_POST['town'];
		$result = mysql_query("UPDATE band_profile SET county = '$county', town = '$town', updated_at=now() WHERE band_name = '$band_name'");
	}
	elseif (isset($_POST['amountOfMembers'])) {

		$amountOfMembers = $_POST['amountOfMembers'];
		$result = mysql_query("UPDATE band_profile SET amountOfMembers = '$amountOfMembers', updated_at=now() WHERE band_name = '$band_name'");
	}
	elseif (isset($_POST['soundCloud'])) {

		$soundCloud = $_POST['soundCloud'];
		$result = mysql_query("UPDATE band_profile SET soundCloud = '$soundCloud', updated_at=now() WHERE band_name = '$band_name'");
	}
	elseif (isset($_POST['image'])) {

		$image = $_POST['image'];
		$result = mysql_query("UPDATE band_profile SET image = '$image', updated_at=now() WHERE band_name = '$band_name'");
	}
	elseif (isset($_POST['webpage'])) {

		$webpage = $_POST['webpage'];
		$result = mysql_query("UPDATE band_profile SET webpage = '$webpage', updated_at=now() WHERE band_name = '$band_name'");
	}
	elseif (isset($_POST['user_name'])) {

		$user_name = $_POST['user_name'];
		$result = mysql_query("UPDATE band_profile SET user_name = '$user_name', updated_at=now() WHERE band_name = '$band_name'");
	}
	elseif (isset($_POST['user_accepted'])) {

		$user_accepted = $_POST['user_accepted'];
		$result = mysql_query("UPDATE band_profile SET user_accepted = '$user_accepted', updated_at=now() WHERE band_name = '$band_name'");
	}
	elseif (isset($_POST['bio'])) {

		$bio = $_POST['bio'];
		$result = mysql_query("UPDATE band_profile SET bio = '$bio', updated_at=now() WHERE band_name = '$band_name'");
	}
	else {

		$followers = $_POST['followers'];
		$result = mysql_query("UPDATE band_profile SET followers = '$followers' WHERE band_name = '$band_name'");
	}
 
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Profile successfully updated.";
 
        // echoing JSON response
        echo json_encode($response);
		
    } else {
		// successfully updated
        $response["success"] = 0;
        $response["message"] = "Profile not successful.";
 
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
