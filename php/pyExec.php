<?php 
$zip = $_GET['zip'];
$warm = $_GET['warm'];
$cold = $_GET['cold'];

echo shell_exec('cd ..');

# Force numeric temperatures
if (!is_numeric($warm))
	$warm = "";
if (!is_numeric($cold))
	$cold = "";

$output = shell_exec('python Weather.py '.$zip.' '.$cold.' '.$warm); 

// Replace degrees with symbol
$output = str_replace("degrees", "&#176", $output);

// Replace Fahrenheit with F
$output = str_replace(" Fahrenheit", "F", $output);

echo nl2br($output);
?>