<head>
	<?php 
	include("php/htmlhead.php"); 
	?>
</head>
<body>

<div class="starter-template" id="content" style="border: 1px solid;height:570px;">
    <h1>
    	<a href="index.php">Attire Decider</a> Weather Analyzer
    </h1>
    <h4>
    	Enter your zip code and you will know what to wear for the day!
    </h4>


    <form action="index.php" method="get">
        <?php echo 'Zip Code: <input type="text" name="zip" value="'.$_GET['zip'].'"><br><br>'; ?>
        <?php echo 'What temperature do you consider warm? <input type="text" name="warm" value="'.$_GET['warm'].'">&#176F<br><br>'; ?>
        <?php echo 'What temperature do you consider cold? <input type="text" name="cold" value="'.$_GET['cold'].'">&#176F<br><br>'; ?>

        <button class="btn btn-block btn-primary" style="width:30%;position:relative;left:350px" type="submit">Go</button>
    </form>
    <hr>
    <!-- The content: python output -->
	<?php include("php/pyExec.php");?>
    <!-- End content: python output -->
    <hr>
    <div style="clear:both">

</div>


<br>
<div style="position:relative; width: 100%; text-align:center">
	<!-- Insert footer -->
	<ul style="list-style-type: none; position:relative; left:-20px;">
	<li><a href="about.php"> About Attire Decider (Coming Soon)</a></li>
	<li>Developed at Princeton University, 2015</li>
    <li><a href="data">View Data Files</a></li>
	</ul>
</div>
</body> 


