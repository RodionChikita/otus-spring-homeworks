package ru.otus.hw;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int quantityOfNumbers = scanner.nextInt();
        int permutations = scanner.nextInt();
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < quantityOfNumbers; i++) {
            if (scanner.hasNextInt()) {
                numbers.add(scanner.nextInt());
            }
        }
        long result = difference(quantityOfNumbers, permutations, numbers);
        System.out.println(result);
    }


    public static long difference(int quantityOfNumbers, int permutations, List<Integer> numbers) {
        long difference = 0;
        long originalSum = Collections.max(numbers);
        List<Integer> numberOfDigits = new ArrayList<>();
        List<List<Integer>> listOfDigits = new ArrayList<>();
        for (int number : numbers) {
            numberOfDigits.add((int) (Math.log10(number) + 1));
            listOfDigits.add(Arrays
                    .stream(String.valueOf(number).split(""))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList()));
        }
        for (int maxDigit = Collections.max(numberOfDigits); permutations != 0 && maxDigit > 0; maxDigit--) {
            Map<Integer, Integer> digitsMap = new TreeMap<>();
            for (int i = 0; i < listOfDigits.size(); i++) {
                if (listOfDigits.get(i).size() >= maxDigit) {
                    if (listOfDigits.get(i).get(listOfDigits.get(i).size() - maxDigit) != 9) {
                        digitsMap.put(i, listOfDigits.get(i).get(listOfDigits.get(i).size() - maxDigit));
                    }
                }
            }
            for (int i = 0; i < digitsMap.size() && permutations != 0; i--) {
                int originalDigit = digitsMap.values().stream().min(Integer::compareTo).get();
                Set<Map.Entry<Integer, Integer>> entrySet = digitsMap.entrySet();
                int originalDigitKey = 0;
                for (Map.Entry<Integer, Integer> pair : entrySet) {
                    if (originalDigit == pair.getValue()) {
                        originalDigitKey = pair.getKey();
                    }
                }
                digitsMap.put(originalDigitKey, 9);
                difference += (long) ((9 - originalDigit) * Math.pow(10, maxDigit - 1));
                permutations--;
            }
        }
        return difference;
    }

}