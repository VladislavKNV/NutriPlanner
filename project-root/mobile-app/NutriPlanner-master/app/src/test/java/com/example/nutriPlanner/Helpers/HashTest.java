package com.example.nutriPlanner.Helpers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HashTest {

    @Test
    public void getHash() {
        PasswordEncryptor hash = new PasswordEncryptor();
        String actual = hash.encryptPassword("12345678");
        String expected = "ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f";

        assertEquals(expected, actual);

    }
}