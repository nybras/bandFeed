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
    $result = mysql_query("SELECT `username` FROM `users` WHERE `username` = '$name' AND `password` = '$password'");
		
	if ($result) {

		$result2 = mysql_query("SELECT `band` FROM `band_members` WHERE `user_accepted` ='$name'");  

       	if (mysql_num_rows($result2) > 0) {
	
 		$response["bands"] = array();

           	while ($row = mysql_fetch_array($result2)) {
			array_push($response["bands"], $row);
			}

		$response["success"] = 1;
		$response["success2"] = 1;
		
		} else {
			$response["success"] = 1;
			$response["success2"] = 0;
		}
 
		// echoing JSON response
		echo json_encode($response);
	} else {
		// username or passwordincorrect
		$response["success"] = 0;
		$response["message"] = "Username or Password incorrect";
		$response["success2"] = 0;
 
		// echo no users JSON
		echo json_encode($response);
	}
}
 else {
    // no username or password entered
    $response["success"] = 0;
    $response["message"] = "No Username or Password entered";
	$response["success"] = 0; 
    // echo no users JSON
    echo json_encode($response);
}

?>