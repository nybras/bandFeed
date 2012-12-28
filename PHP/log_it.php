<?php 

    $message=$_REQUEST['message'];

 date_default_timezone_set('Europe/London');
 $date = date('d-m-Y H:i:s', time());

 $File = "log.txt"; 
 $Handle = fopen($File, 'a');
 $Data = "{$date} - {$message}\n"; 
 fwrite($Handle, $Data); 
 fclose($Handle); 
 ?>