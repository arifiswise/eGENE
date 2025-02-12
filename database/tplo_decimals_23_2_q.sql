﻿USE egene;

CREATE TABLE `tplo_decimals_23_2_q` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(2048) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;


insert into `tplo_decimals_23_2_q`(`id`,`value`) values (1,'<br />(a) How many good $goodsPl were sold yesterday?<br />
(b) How much less money did the music shop collect yesterday because of defective $goodsPl?
||
(a)<br />
For every $saleTotal $goodsPl sold, its revenue = ($saleTotal - $saleDefect) x $priceNormal + $saleDefect x $priceDefect = $saleTotalSet<br />
$saleYesterday/$saleTotalSet = $setCount<br />
Number of $goodsPl were sold yesterday = ($saleTotal - $saleDefect) x $setCount = $answerA<br />
(b)<br /> 
Total number of defect $goodsPl = $saleDefect x $setCount = $answerBTmp<br />
$answerBTmp x ($priceNormal - $priceDefect) = $answerB<br />
It collected ${currency}$answerB less money because of defective $goodsPl
||
$answerA|$answerB');
