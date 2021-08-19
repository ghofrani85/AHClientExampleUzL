package eu.arrowhead.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class TestController {
    @GetMapping(path = "/random/string")
    public ResponseEntity getRandomString() {
        return new ResponseEntity(RandomStringUtils.random(32, true, true), HttpStatus.OK);
    }

    @GetMapping(path = "/random/integer")
    public ResponseEntity getRandomInteger() {
        return new ResponseEntity(RandomStringUtils.random(32, false, true), HttpStatus.OK);
    }

    @PostMapping(path = "/random/integer")
    public ResponseEntity setRandomInteger(@RequestBody String length) {
        if (!NumberUtils.isCreatable(length))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        return new ResponseEntity(RandomStringUtils.random(Integer.valueOf(length), false, true), HttpStatus.OK);
    }
}
