package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.databaseTools.DatabaseTools;

public class Tools {


	
	public static void writeToFile(String data , String fileName) throws Exception {
		File fout = new File(fileName);
		
		FileWriter fw = new FileWriter(fout,true);
		
		BufferedWriter bw = new BufferedWriter(fw);
	 
		bw.write(data);
		bw.newLine();
	 
		bw.close();
	}
	
	//create string from repeated chars 
	public static String repeatedString(char c , int n) {
		char[] chars = new char[n];
		Arrays.fill(chars, c);
		return new String(chars);
	}
	
	//check if string started with number or character 
	public static boolean checkIfStringContainsWithNumberOrChar(String inputString) {
		
		if(Character.isDigit(inputString.charAt(0))) return true;
		
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(inputString);
		boolean b = m.find();
		
		if (b) {
			return true;
		}

		return false ;
	}
	
	
	private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false; 
	    }
	    return pattern.matcher(strNum).matches();
	}
	
	public static <K extends Comparable, V> Map<K, V> sortByKeys(Map<K, V> map)
    {
        // create a list of map keys and sort it
        List<K> keys = new ArrayList(map.keySet());
        Collections.sort(keys);
 
        // create an empty insertion-ordered `LinkedHashMap`
        Map<K, V> linkedHashMap = new LinkedHashMap<>();
 
        // for every key in the sorted list, insert key-value
        // pair in `LinkedHashMap`
        for (K key: keys) {
            linkedHashMap.put(key, map.get(key));
        }
 
        return linkedHashMap;
    }
	
	
	public static int  countFrequencies(ArrayList<String> list,String element)
    {
        // hashmap to store the frequency of element
        Map<String, Integer> hm = new HashMap<String, Integer>();
        //integer to store the frequency of element;
        int frq = 0 ;
 
        for (String i : list) {
            Integer j = hm.get(i);
            hm.put(i, (j == null) ? 1 : j + 1);
        }
 
        // displaying the occurrence of elements in the arraylist
        if(!list.contains(element)) return 0;
        frq = hm.get(element);
        return frq;
    }
    public static <K, V> K getKeyOfValueFromMap(Map<K, V> map, V value)
    {
        return map.entrySet().stream()
                .filter(entry -> value.equals(entry.getValue()))
                .findFirst().map(Map.Entry::getKey)
                .orElse(null);
    }
 
    
    public static void clearFile(String fileName) throws Exception {
    	BufferedWriter bfWriter = new BufferedWriter(new FileWriter(new File(fileName)));
    	bfWriter.write("");
    	bfWriter.close();
    }
	
	public static void main(String[] args) {
		ArrayList<String> list= new ArrayList<String> (); list.addAll(Arrays.asList("select","select","select","*","*"));
		System.out.println(countFrequencies(list, "select"));
	}
}
