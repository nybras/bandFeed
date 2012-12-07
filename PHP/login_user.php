<?php
 
 
// array for JSON response
$response = array();
 
 
// check for post data
if (isset($_GET["name"]) && isset($_GET["password"])) {
    $name = $_GET['name'];
	$password = $_GET['password'];
	
	// include db connect class
	require_once __DIR__ . '/db_connect.php';
 
	// connecting to db
	$db = new DB_CONNECT();
 
    // get a product from products table
    $result = mysql_query("SELECT `name` FROM `busers` WHERE `name` = '$name' AND `password` = '$password'");
	
	//if (strcmp($result,$password)) {
	if ($result) {
	
	    // success
		$response["success"] = 1;
 
		// echoing JSON response
		echo json_encode($response);
	} else {
		// username or passwordincorrect
		$response["success"] = 0;
		$response["message"] = "Username or Password incorrect";
 
		// echo no users JSON
		echo json_encode($response);
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