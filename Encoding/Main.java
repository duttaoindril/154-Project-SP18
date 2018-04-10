package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static HashMap<String, String> letterLookup;

    public static void main(String[] args) {
        File inputFile = new File(args[0]);

        try {
            String encodedTM = "";
            Scanner sc = new Scanner(inputFile);
            letterLookup = new HashMap<>();
            String initialState = "";
            String finalStates = "";


            while (sc.hasNextLine()) {
                String i = sc.nextLine();
                // TODO: Capture final and initial states, make initial state first transition
                // Capture and encode initial and final states
                if (i.contains("block id=")) {
                    String stateId = i.substring(i.indexOf("\"") + 1, i.indexOf("name") - 2);
                    stateId = stateToUnary(stateId);
                    // scan until end of block
                    while (!i.contains("/block")) {
                        i = sc.nextLine();
                        if (i.contains("initial")) {
                            initialState = stateId;
                        }
                        if (i.contains("final")) {
                            if (finalStates.length() == 0)
                                finalStates += stateId;
                            else
                                finalStates += 0 + stateId;
                        }
                    }
                }
                // Capture and encode transitions
                else if (i.contains("<transition")) {
                    String fromState = sc.nextLine();
                    String toState = sc.nextLine();
                    String read = sc.nextLine();
                    String write = sc.nextLine();
                    String move = sc.nextLine();

                    fromState = getStringBetweenDiv(fromState);
                    toState = getStringBetweenDiv(toState);
                    read = getStringBetweenDiv(read).toLowerCase();
                    write = getStringBetweenDiv(write).toLowerCase();
                    move = getStringBetweenDiv(move);

                    fromState = stateToUnary(fromState);
                    toState = stateToUnary(toState);

                    read = symbolToUnary(read);
                    write = symbolToUnary(write);

                    move = (move.equals("R") ? "11" : "1");

                    String encodedTransition = "D" + fromState + 0 + read + 0 + toState +
                            0 + write + 0 + move;

                    // add encoded transition to start of string if it's initial
                    if (fromState.equals(initialState))
                        encodedTM = encodedTransition + encodedTM;
                    else
                        encodedTM += encodedTransition;
                }

            }

            encodedTM += "F" + finalStates;
            System.out.println(encodedTM);
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String symbolToUnary(String inputSymbol) {
        // handle blank
        if (inputSymbol.equals("empty")) {
            return "1";
        }
        if (!letterLookup.containsKey(inputSymbol)) {
            // create key
            letterLookup.put(inputSymbol, stateToUnary(Integer.toString(letterLookup.size())) + 1);
        }
        return letterLookup.get(inputSymbol);
    }

    private static String getStringBetweenDiv(String input) {
        String returnValue;
        // blank (square) input
        if (input.contains("/>")) {
            returnValue = "empty";
        } else {
            // extract value between > and < if not a blank div
            returnValue = input.substring(input.indexOf(">") + 1, input.lastIndexOf("<"));
        }
        return returnValue;
    }

    private static String stateToUnary(String input) {
        String unaryOutput = "";
        for (int i = 0; i <= Integer.parseInt(input); i++) {
            unaryOutput += "1";
        }
        return unaryOutput;
    }
}

