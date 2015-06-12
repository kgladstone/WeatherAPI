<?php 
$zip = $_GET['zip'];

echo shell_exec('cd ..');

$output = shell_exec('python Weather.py '.$zip); 

// Replace degrees with symbol
$output = str_replace("degrees", "&#176", $output);

// Replace Fahrenheit with F
$output = str_replace(" Fahrenheit", "F", $output);

echo nl2br($output);
?>