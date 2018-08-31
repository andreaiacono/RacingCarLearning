package me.andreaiacono.racinglearning;

import me.andreaiacono.racinglearning.core.GameParameters;
import me.andreaiacono.racinglearning.gui.MainFrame;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        GameParameters params = new GameParameters(args);
        new MainFrame(params);
//
//        char[][] letters = {
//                {'C', 'F', 'J', 'V', 'W', 'U'},
//                {'D', 'E', 'F', 'I', 'T', 'V'},
//                {'E', 'F', 'R', 'T', 'W', 'Z'},
//                {'A', 'J', 'K', 'R', 'W', 'Y'},
//                {'D', 'Q', 'R', 'T', 'U', 'X'},
//                {'C', 'D', 'J', 'K', 'L', 'Z'}
//        };
//
//        Set<String> words = new HashSet<>();
//        try (Scanner sc = new Scanner(new File("/Users/aiacono/words6.txt"))) {
//            while (sc.hasNextLine()) {
//                words.add(sc.nextLine().toUpperCase());
//            }
//        }
//
//        for (int i1 = 0; i1 < 6; i1++) {
//            for (int i2 = 0; i2 < 6; i2++) {
//                for (int i3 = 0; i3 < 6; i3++) {
//                    for (int i4 = 0; i4 < 6; i4++) {
//                        for (int i5 = 0; i5 < 6; i5++) {
//                            for (int i6 = 0; i6 < 6; i6++) {
//                                String word = "" + letters[0][i1] + letters[1][i2] + letters[2][i3] + letters[3][i4] + letters[4][i5] + letters[5][i6];
//                                if (words.contains(word)) {
//                                    System.out.println(word);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}
