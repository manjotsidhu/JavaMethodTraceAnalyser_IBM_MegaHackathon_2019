/*
 * Copyright 2018 Manjot Sidhu <manjot.techie@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.manjotsidhu.mta.tools;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utilities used in developing analyser.
 * 
 * @author Manjot Sidhu
 */
public class Tools {

    /**
     * Finds the specified <code>number</code> in a <code>arr</code> starting
     * from <code>startIndex</code> and returns the index of the array.
     *
     * @param arr ArrayList <code>&lt;</code> Integer <code>&gt;</code> input array
     * @param startIndex Integer starting index from where to start the search.
     * @param number Integer element to find in <code>arr</code>.
     * @return index of the array, if nothing found then returns 0.
     */
    public static int find(ArrayList<Integer> arr, int startIndex, int number) {
        int result = -1;
        for (int iteration = startIndex; iteration < arr.size(); iteration++) {
            if (arr.get(iteration) == number) {
                result = iteration;
                break;
            }
        }
        return result;
    }
    
    /**
     * Finds the specified <code>number</code> in a <code>arr</code> starting
     * from <code>startIndex</code> and returns the index of the array.
     *
     * @param arr ArrayList <code>&lt;</code> Integer <code>&gt;</code> input array
     * @param startIndex Integer starting index from where to start the search.
     * @param number Integer element to find in <code>arr</code>.
     * @return index of the array, if nothing found then returns 0.
     */
    public static int find(ArrayList arr, int startIndex, Object number) {
        int result = -1;
        for (int iteration = startIndex; iteration < arr.size(); iteration++) {
            if (arr.get(iteration).equals(number)) {
                result = iteration;
                break;
            }
        }
        return result;
    }

    /**
     * Finds the specified <code>number</code> in an <code>arr</code> starting
     * from <code>startIndex</code> and returns the index of the array.
     *
     * @param arr ArrayList input array
     * @param startIndex Integer starting index from where to start the search.
     * @param number String element to find in <code>arr</code>.
     * @return index of the array, if nothing found then returns -1.
     */
    public static int find(ArrayList arr, int startIndex, String number) {
        int result = -1;
        for (int iteration = startIndex; iteration < arr.size(); iteration++) {
            if (arr.get(iteration).equals(number)) {
                result = iteration;
                break;
            }
        }
        return result;
    }

    /**
     * Finds the specified <code>number</code> in an <code>arr</code> starting
     * from <code>startIndex</code> and returns the index of the array.
     *
     * @param arr Object[] input array
     * @param startIndex Integer starting index from where to start the search.
     * @param number String element to find in <code>arr</code>.
     * @return index of the array, if nothing found then returns 0.
     */
    public static int find(Object[] arr, int startIndex, String number) {
        int result = -1;
        for (int iteration = startIndex; iteration < arr.length; iteration++) {
            if (arr[iteration].equals(number)) {
                result = iteration;
                break;
            }
        }
        return result;
    }
    
    /**
     * Finds the specified <code>number</code> in an <code>arr</code> starting
     * from <code>startIndex</code> and returns the index of the array.
     *
     * @param arr Object[] input array
     * @param startIndex Integer starting index from where to start the search.
     * @param number String element to find in <code>arr</code>.
     * @return index of the array, if nothing found then returns 0.
     */
    public static int find(Object[] arr, int startIndex, Object number) {
        int result = -1;
        for (int iteration = startIndex; iteration < arr.length; iteration++) {
            if (arr[iteration].equals(number)) {
                result = iteration;
                break;
            }
        }
        return result;
    }
    
    /**
     * Counts the number of <code>element</code> in the given <code>arr</code>.
     *
     * @param arr ArrayList<code>&lt;</code> String <code>&gt;</code> input array in which to count.
     * @param element String element to count.
     * @return number of elements exist in the array.
     */
    public static int count(ArrayList<String> arr, String element) {
        int count = 0;
        if (arr.contains(element)) {
            for (int iteration = 0; iteration < arr.size(); iteration++) {
                if (arr.get(iteration).contains(element)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * removeDuplicates simply removes duplicate elements from the given array
     * <code>arr</code> and returns a new array containing all unique elemnts.
     *
     * @param arr ArrayList<code>&lt;</code> String <code>&gt;</code> input array.
     * @return new array having unique elements.
     */
    public static ArrayList removeDuplicates(ArrayList<String> arr) {
        String[] tempArr = new String[arr.size()];
        ArrayList<String> newArr = new ArrayList<>();

        arr.toArray(tempArr);
        tempArr = Arrays.stream(tempArr).distinct().toArray(String[]::new);

        newArr.addAll(Arrays.asList(tempArr));
        return newArr;
    }
    
    /**
     * removeDuplicates simply removes duplicate elements from the given array
     * <code>arr</code> and returns a new array containing all unique elemnts.
     *
     * @param arr ArrayList<code>&lt;</code> String <code>&gt;</code> input array.
     * @return new array having unique elements.
     */
    public static ArrayList removeDuplicatesLong(ArrayList<Long> arr) {
        Long[] tempArr = new Long[arr.size()];
        ArrayList newArr = new ArrayList();

        arr.toArray(tempArr);
        tempArr = Arrays.stream(tempArr).distinct().toArray(Long[]::new);

        newArr.addAll(Arrays.asList(tempArr));
        return newArr;
    }

    /**
     * Converts 2D ArrayList to Object[][].
     * 
     * @param arr Input array to be converted
     * @param y length of input array
     * @return new array of Object[][] datatype
     */
    public static Object[][] toArray(ArrayList arr, int y) {
        Object[][] newArr = new Object[y][arr.size()];

        try {
            for (int i = 0; i < arr.size(); i++) {
                for (int j = 0; j < ((ArrayList) arr.get(i)).size(); j++) {
                    newArr[j][i] = ((ArrayList) arr.get(i)).get(j);
                }
            }
        } catch (Exception e) {
            System.err.println("Tools: toArray [FAILED]");
            e.printStackTrace();
        }
        return newArr;
    }
    
    /**
     * Converts 2D ArrayList to Object[][].
     * 
     * @param arr Input array to be converted
     * @param y length of input array
     * @return new array of Object[][] datatype
     */
    public static String[][] toStringArray(ArrayList arr, int y) {
        String[][] newArr = new String[y][arr.size()];

        try {
            for (int i = 0; i < arr.size(); i++) {
                for (int j = 0; j < ((ArrayList) arr.get(i)).size(); j++) {
                    newArr[j][i] = String.valueOf(((ArrayList) arr.get(i)).get(j));
                }
            }
        } catch (Exception e) {
            System.err.println("Tools: toString Array [FAILED]");
            e.printStackTrace();
        }
        return newArr;
    }
    
    /**
     * Converts 2D ArrayList to Object[][].
     * 
     * @param arr Input array to be converted
     * @param y length of input array
     * @return new array of Object[][] datatype
     */
    public static String[][] toStringArrayAlt(ArrayList arr, int y) {
        String[][] newArr = new String[arr.size()][y];

        try {
            for (int i = 0; i < arr.size(); i++) {
                for (int j = 0; j < ((ArrayList) arr.get(i)).size(); j++) {
                    newArr[i][j] = String.valueOf(((ArrayList) arr.get(i)).get(j));
                }
            }
        } catch (Exception e) {
            System.err.println("Tools: toStringArrayAlt [FAILED]");
            e.printStackTrace();
        }
        return newArr;
    }
    
    /**
     * Converts 2D ArrayList to Object[][].
     * 
     * @param arr Input array to be converted
     * @param y length of input array
     * @return new array of Object[][] datatype
     */
    public static String[][] toStringArrayAlt(ArrayList arr, int y, String[] files) {
        String[][] newArr = new String[arr.size()][y+1];
        newArr[0][0] = "log Files";

        try {
            for (int i = 0; i < arr.size(); i++) {
                if(i!=0) 
                    newArr[i][0] = files[i-1];
                for (int j = 1; j < ((ArrayList) arr.get(i)).size()+1; j++) {
                    newArr[i][j] = String.valueOf(((ArrayList) arr.get(i)).get(j-1));
                }
            }
        } catch (Exception e) {
            System.err.println("Tools: toStringArrrayAlt [FAILED]");
            e.printStackTrace();
        }
        return newArr;
    }
    
    /**
     * Converts 2D ArrayList to Object[][].
     * 
     * @param arr Input array to be converted
     * @param y length of input array
     * @return new array of Object[][] datatype
     */
    public static String[][] toStringArray(ArrayList arr, int y, String[] files) {
        String[][] newArr = new String[y+1][arr.size()];
        try {
            for (int i = 0; i < arr.size(); i++) {
                    newArr[0][i] = files[i];
                for (int j = 1; j < ((ArrayList) arr.get(i)).size()+1; j++) {
                    String s = String.valueOf(((ArrayList) arr.get(i)).get(j-1));
                    newArr[j][i] = s;
                }
            }
        } catch (Exception e) {
            System.err.println("Tools: toStringArray [FAILED]");
            e.printStackTrace();
        }
        return newArr;
    }
    
    /**
     * Converts 2D ArrayList to Object[][].
     * 
     * @param arr Input array to be converted
     * @param y length of input array
     * @return new array of Object[][] datatype
     */
    public static String[][] toStringArrayMethods(ArrayList arr, int y, String[] files) {
        ArrayList<String> buf = new ArrayList<>();
        String[][] newArr = new String[y+1][arr.size()];
        try {
            for (int i = 0; i < arr.size(); i++) {
                newArr[0][i] = files[i];
                for (int j = 1; j < ((ArrayList) arr.get(i)).size()+1; j++) {
                    String s = String.valueOf(((ArrayList) arr.get(i)).get(j-1));
                    if (buf.contains(s)) {
                        buf.remove(s);
                        s += " <out>";
                    } else {
                        buf.add(s);
                        s += " <in>";
                    }
                    newArr[j][i] = s;
                }
                buf = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Tools: toStringArray [FAILED]");
            e.printStackTrace();
        }
        return newArr;
    }
        
    /**
     * Finds one odd (unique) element in 1D array and return the index of that element.
     * 
     * @param arr input array
     * 
     * @param startIndex starting index to start from
     * @return index of the unique element
     */
    public static int anomaly(Object[] arr, int startIndex) {

        int flag = 0;

        for (int i = startIndex; i < arr.length; i++) {
            for (int j = startIndex; j < arr.length; j++) {
                if (i != j) {
                    if (arr[i] != arr[j]) {
                        flag = 1;
                    } else {
                        flag = 0;
                        break;
                    }
                }
            }
            if (flag == 1) {
                return i;
            }
        }

        return 0;
    }

    /**
     * extends ArrayList size to a certain length by inserting null values
     * 
     * @param arr input array
     * @param length length upto which we want to extend
     */
    public static void extendArrayLength(ArrayList arr, int length) {
        while (arr.size() < length) {
            arr.add(null);
        }
    }

    /**
     * extends ArrayList size to a certain index by inserting null values
     * 
     * @param arr input array
     * @param index length upto which we want to extend
     */
    public static void extendArrayIndex(ArrayList arr, int index) {
        while (arr.size() < index + 1) {
            arr.add(null);
        }
    }
    
    /**
     * calculates the max size of the sub-array of a nested ArrayList.
     * 
     * @param arr input array
     * @return integer of max size
     */
    public static int maxInnerArraySize(ArrayList arr) {
        int max = 0;
        
        for (int i = 0; i < arr.size(); i++) {
            int s = ((ArrayList) arr.get(i)).size();
            if (s > max)
                max = s;
        }
        
        return max;
    }
    
    public static Color colorPicker(int number) {
        while (!(number <= 8)) {
            number %= 8;
        }
       
        switch (number) {
            case 1: return Color.web("#F2612D");
            case 2: return Color.web("#FBA51B");
            case 3: return Color.web("#57B757");
            case 4: return Color.web("#41A9C9");
            case 5: return Color.web("#4258C9");
            case 6: return Color.web("#9942C8");
            case 7: return Color.web("#C74164");
            case 8: return Color.web("#878787");
            default: return null;
        }
    }
}
