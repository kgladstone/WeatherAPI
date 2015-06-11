<?php 
$input = $_GET['zip'];

// Replace quotations with escape keys
#$input = str_replace("\"", "\\\"", $input);
#echo $input;
echo shell_exec('cd ..');
#echo $input;
$output = shell_exec('java Weather data.txt '.$input); 

// Replace degrees with symbol
$output = str_replace("degrees", "&#176", $output);

// Replace Fahrenheit with F
$output = str_replace(" Fahrenheit", "F", $output);

echo nl2br($output);
#echo $output;?>