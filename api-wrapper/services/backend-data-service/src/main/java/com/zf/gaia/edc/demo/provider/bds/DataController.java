package com.zf.gaia.edc.demo.provider.bds;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/data")
public class DataController {

    Map<String, String> asset = new HashMap<>();

    @GetMapping("/{id}")
    public String get(@PathVariable("id") String contractOfferId) {
        if (!asset.containsKey(contractOfferId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The contract offer id was not found in database");
        }

        System.out.println("Returning data for contract offer " + contractOfferId);

        return asset.get(contractOfferId);
    }

    @PostMapping("/{id}")
    public void store(@PathVariable("id") String contractOfferId, @RequestBody String data) {
        System.out.println("Saving data for contract offer " + contractOfferId);

        asset.put(contractOfferId, data);
    }
}
