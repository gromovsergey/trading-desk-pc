<?php

Print "Args: ";
foreach (array_slice($argv, 1) as $a) {
    Print $a." ";
}
Print "\n\n";

#Modifying <include path>
$includePath = get_include_path().$argv[2];
Print "Include Path = ".$includePath;
set_include_path($includePath);

require_once('const/Logger.php');
require_once('Log.php');
require_once('Log/composite.php');
require_once('Log/console.php');
require_once('Foros.php');
require_once('AdvertisingChannelTest.php');
require_once('DiscoverChannelTest.php');
require_once('CampaignTest.php');
require_once('CampaignCreativeGroupTest.php');
require_once('CommonTest.php');
require_once('TextAdvertisingReportTest.php');
require_once('ReferrerReportTest.php');

date_default_timezone_set("GMT");

//Setup logger

$logName = getcwd()."/target/tests.log";
$logger = new Log_composite(Logger::GLOBAL_LOGGER_NAME);
$log = Log::singleton('console', '', '', null, Logger::INFO);
$logger->addChild($log);
$logger->info("Log file location: $logName");
$log = Log::singleton('file', "$logName", '', null, Logger::DEBUG);
$logger->addChild($log);

@$props = getProperties($argv[1]);
$tests = createTests($props, $logger);

$failedNum = 0;
$totalNum = 0;

foreach ($tests as $test) {
    $class = get_class($test);
    $rc = new ReflectionClass($class);
    $methods = $rc->getMethods(ReflectionMethod::IS_PUBLIC);
    foreach ($methods as $method) {
        if (preg_match('/^test.*/', $method->name) && $method->getNumberOfParameters() == 0) {
            $failedNum += runBare($test, $method) ? 0 : 1;
            $totalNum++;
        }
    }
}

function runBare($test, $method) {
    $logMarker = "<".$test.">.<".$method->name."> ";
    global $logger;
    try {
        $logger->info("Started ".$logMarker);
        call_user_func(array($test, $method->name));
        $logger->info("Finished ".$logMarker);
        return true;
    } catch (Exception $ex) {
        $logger->err($logMarker . "Failed ".$ex->getMessage()."\n".$ex->getTraceAsString());
        return false;
    }
}


$logger->info("(".($totalNum - $failedNum).") tests SUCCEEDED, (".$failedNum.") FAILED from (".$totalNum.") TOTAL.");

if ($failedNum > 0) {
    $logger->err("There are ".$failedNum." failed tests. Please check log.");
    exit(1);
}

$logger->info("All tests are OK.");



function getProperties($location) {
    #hardcoded values
    $result = array();

    $props = $location == null || strlen($location) == 0? null: file($location);
    if ($props == null) {
        fwrite(STDERR, "WARNING: Properties file is not provided (default values will be used)\nUsage: php client-test.php <props file location>\n");
        return $result;
    }

    #values from props file
    foreach ($props as $prop) {
        $paramName = strtok($prop, '=');
        $paramVal = trim(strtok('='));
        if (strlen($paramName) > 0 && strlen($paramVal) > 0) {
            $paramVal = str_replace("\\\\", "\\", $paramVal);
            $paramVal = str_replace("\\:", ":", $paramVal);
            $result[$paramName] = $paramVal;
        }
    }

    return $result;
}

function createTests($props, $logger) {
    $foros = Foros::create($props["foros.base"], $props["foros.userToken"], $props["foros.key"], @$props["foros.proxy"]);
    $forosAdvertiser = Foros::create($props["foros.base"], $props["foros.advertiser.userToken"], $props["foros.advertiser.key"], @$props["foros.proxy"]);
    $forosPublisher = Foros::create($props["foros.base"], $props["foros.publisher.userToken"], $props["foros.publisher.key"], @$props["foros.proxy"]);

    $result = array(
        //Internal tests
        new CommonTest($props["foros.base"], $props["foros.userToken"], $props["foros.key"],
            $logger, $props["foros.test.advertiser.id"], $props["foros.test.user.id"]),
        new CampaignTest($foros, $logger, false,
            $props["foros.test.advertiser.id"], $props["foros.test.user.id"]),
        new CampaignCreativeGroupTest($foros, $logger, false,
            $props["foros.test.advertiser.id"], $props["foros.test.user.id"]),
        new DiscoverChannelTest($foros, $logger,
            $props["foros.test.agency.id"]),
        new AdvertisingChannelTest($foros, $logger, false,
            $props["foros.test.agency.id"], $props["foros.test.channel.advertising.id"]),
        new TextAdvertisingReportTest($foros, $logger, false, $props["foros.test.agency.id"]),

        //External tests
        new CampaignTest($forosAdvertiser, $logger, true,
            $props["foros.test.advertiser.id"], $props["foros.test.user.id"]),
        new CampaignCreativeGroupTest($forosAdvertiser, $logger, true,
            $props["foros.test.advertiser.id"], $props["foros.test.user.id"]),
        new AdvertisingChannelTest($forosAdvertiser, $logger, true, $props["foros.test.agency.id"],
            $props["foros.test.channel.advertising.id"]),
        new TextAdvertisingReportTest($forosAdvertiser, $logger, true, $props["foros.test.agency.id"]),
        new ReferrerReportTest($forosPublisher, $logger, true, $props["foros.test.site.id"]),

    );

    return $result;
}
