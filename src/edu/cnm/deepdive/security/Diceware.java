package edu.cnm.deepdive.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Diceware {

  private static final String LINE_PATTERN = "^\\s*(\\d+)\\s+(\\S+)\\s*$";
  
  
  private Map<String, String> words;
  private Random rng;
  private int numDice = 0;

  /**
   * 
   * @param file
   * @throws FileNotFoundException
   * @throws IOException
   * @throws NoSuchAlgorithmException
   */
  public Diceware(File file) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
    words = new HashMap<>();
    rng = SecureRandom.getInstanceStrong();
    try ( // try will auto close anything that fail
        FileInputStream input = new FileInputStream(file); // open to read
        InputStreamReader reader = new InputStreamReader(input);
        BufferedReader buffer = new BufferedReader(reader);) {
      Pattern p = Pattern.compile(LINE_PATTERN);
      for (String line = buffer.readLine(); line != null; line = buffer.readLine()) {
        Matcher m = p.matcher(line);
        if (m.matches()) {
          if (numDice == 0) {
            numDice = m.group(1).length();
          }
          words.put(m.group(1), m.group(2));
        }
        // TODO process dice key and work from Line.

      }

    }
  }

  public String[] generate(int length) {
    String[] passphrase = new String[length];
    for (int i = 0; i < length; i++) {
      passphrase[i] = generate();
    }
    return passphrase;
  }

  private String generate() {
    String key = "";
    for (int roll = 0; roll < numDice; roll++) {
      int value = rng.nextInt(6) + 1;
      key += value;
    }
    return words.get(key);
  }
  
}
