package com.atm.simulator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ATM atm = new ATM();
        try (Scanner scanner = new Scanner(System.in)) {
            atm.startSession(scanner);
        }
    }
}
