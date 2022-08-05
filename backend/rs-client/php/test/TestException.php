<?php

class TestException extends Exception {

    public function __construct($msg) {
        Exception::__construct($msg);
    }
}
