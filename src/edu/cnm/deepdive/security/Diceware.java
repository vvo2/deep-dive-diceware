package edu.cnm.deepdive.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author vovo at Deep Dive Coding Java Cohort 3
 *
 */
public class Diceware {

  private static final String NEGATIVE_PASSPHRASE_MESSAGE = "Passphrase length cannot be negative.";

  private static final String LINE_PATTERN = "^\\s*(\\d+)\\s+(\\S+)\\s*$";
  
  private List<String> words;
  private Random rng = null;

  /**
   * Initializes an instance of<code>Diceware</code> using a reference to an object
   * {@link java.io.File}.  with exceptions to throws
   * @param file                    file to read for word list
   * @throws FileNotFoundException  if file does not exist
   * @throws IOException            if file can not be read
   */
  public Diceware(File file) throws FileNotFoundException, IOException {
    words = new ArrayList<>();
    
    try ( // try will auto close anything that fail
        FileInputStream input = new FileInputStream(file); // open to read
        InputStreamReader reader = new InputStreamReader(input);
        BufferedReader buffer = new BufferedReader(reader);) {
      Pattern p = Pattern.compile(LINE_PATTERN);
      for (String line = buffer.readLine(); line != null; line = buffer.readLine()) {
        Matcher m = p.matcher(line);
        if (m.matches()) {
          words.add(m.group(2));
        }
        // TODO process dice key and work from Line.

      }

    }
  }
  
  /**
   * Initializes an instance of<code>Diceware</code> to collect from the source of the word list
   * @param source                  word list
   */
  public Diceware (Collection<String> source) {
    words = new ArrayList<>(source);
  }
  
  /**
   * Initializes an instance of<code>Diceware</code>, ResourceCollection object as a source for the word list
   * 
   * @param bundle                  Property containing word list
   */
  public Diceware(ResourceBundle bundle) {
    words = new ArrayList<>();
    Enumeration<String> en = bundle.getKeys();
    while (en.hasMoreElements()) {
      words.add(bundle.getString(en.nextElement()));
    }
  }
  
  /**
   * return random instance to be uses for selecting word from the word list.  
   * @return                        return random
   * @throws NoSuchAlgorithmException throws
   */
  public Random getRng() throws NoSuchAlgorithmException { 
    if (rng == null) {
      rng = SecureRandom.getInstanceStrong();
    }
    return rng;
  }

  /**
   * sets a reference to the Random instance to be used for selecting words from the word list
   * @param rng                     reference to Random instance
   */
  public void setRng(Random rng) {
    this.rng = rng;
  }

  /**
   * generate and returns a password of the specified length. The inclusion of duplicates is controlled by the dubplcatedAllowed argument.
   * if the specified length is greater than the number of words in the word list, and duplicates aren't allowed, then an infinite loop will result.
   * @param length                      number of word to include in passphrase
   * @param duplicatesAllowed           true if 
   * @return                            words in generated passphrase.
   * @throws NoSuchAlgorithmException   if algorithm for default strong source of randomness is not available.
   * @throws InsufficientPoolException   if password length exceeds word list, and duplicates not allowed or word list has no words.
   * @throws IllegalArgumentException   if requested length is negative.
   */
  public String[] generate(int length, boolean duplicatesAllowed) 
      throws NoSuchAlgorithmException, InsufficientPoolException, IllegalArgumentException {
    if (length < 0 ) {
      throw new IllegalArgumentException(NEGATIVE_PASSPHRASE_MESSAGE);
    }
    if ((words.size() == 0 && length > 0)
        || (!duplicatesAllowed && length > words.size())) {
      throw new InsufficientPoolException();
    }
    List<String> passphrase = new LinkedList<>();
    while (passphrase.size() < length) {
      String word = generate();
      if (duplicatesAllowed || !passphrase.contains(word)) {
        passphrase.add(word);
      }
    }
   // return passphrase.toArray(new String[0]);//0 length array, compiler make the array size
    return passphrase.toArray(new String[passphrase.size()]);
  }
  
  /**
   * Generate and return a password of the specified length. This method simply invokes generate(int length, boolean duplicatesAllowed), specifies duplicate allow
   * @param length                      Number of words for generated passphrase
   * @return                            words in generated passphrase 
   * @throws NoSuchAlgorithmException   if algorithm for default strong source of randomness is not available.
   * @throws InsufficientPoolException  if word list has no words.
   * @throws IllegalArgumentException   if requested length is negative.
   */
  public String[] generate(int length) throws NoSuchAlgorithmException, InsufficientPoolException, IllegalArgumentException {
    return generate(length, true);
  }

  private String generate() throws NoSuchAlgorithmException {
      int index = getRng().nextInt(words.size());
      return words.get(index);
    }
    
  public static class InsufficientPoolException extends IllegalArgumentException {
    private InsufficientPoolException() {
      
    }
  }
  
 }
  

