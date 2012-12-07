<?php
 
 
// array for JSON response
$response = array();
 
 
// check for post data
if (isset($_POST["name"]) && isset($_POST["password"])) {
    $name = $_POST['name'];
	$password = $_POST['password'];
	
	// include db connect class
	require_once __DIR__ . '/db_connect.php';
 
	// connecting to db
	$db = new DB_CONNECT();
 
    // check for existing name in busers table
    $result = mysql_query("SELECT * FROM `busers` WHERE `name` = '$name'");

	if (mysql_num_rows($result) > 0) {
		// username already in use
		$response["success"] = 0;
		$response["message"] = "Username already in usezzz";
 
		// echo no users JSON
		echo json_encode($response);


	} else {
		$result2 = mysql_query("INSERT INTO busers(name, password) VALUES('$name', '$password')");
	
		if ($result2) {

	     // success
		$response["success"] = 1;
 
		// echoing JSON response
		echo json_encode($response);
		}
		else {
		// username was not created
		$response["success"] = 0;
		$response["message"] = "Username not created";
 
		// echo no users JSON
		echo json_encode($response);
		}
	}
}
 else {
    // no username or password entered
    $response["success"] = 0;
    $response["message"] = "No Username or Password entered";
 
    // echo no users JSON
    echo json_encode($response);
}

?>