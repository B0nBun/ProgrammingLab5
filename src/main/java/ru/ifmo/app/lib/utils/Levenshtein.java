package ru.ifmo.app.lib.utils;

import java.util.Collections;
import java.util.List;

/**
 * Class containing functionality (static methods) for computing the levenshtein distance between
 * strings https://en.wikipedia.org/wiki/Levenshtein_distance
 */
public class Levenshtein {
  /**
   * Compute the levenshtein distance between strings recursively, using caching for optimization
   * 
   * @param s1 Full first string
   * @param s2 Full second string
   * @param l1 A length of the first string slice that is considered in current iteration
   *        (s1.slice(0, l1))
   * @param l2 A length of the second string slice that is considered in current iteration
   *        (s2.slice(0, l2))
   * @param cache A two dimensional array that is caching the results of the function, depending on
   *        the l1 and l2 arguments (e.g. cache[2][1] has a levenshtein distance for s1.slice(0, 2)
   *        and s2.slice(0, 1))
   * @return A distance between two slices of strings
   */
  private static int distanceWithCache(String s1, String s2, int l1, int l2, Integer[][] cache) {
    if (cache[l1][l2] != null) {
      return cache[l1][l2];
    }

    if (l1 == 0) {
      cache[l1][l2] = l2;
      return cache[l1][l2];
    }
    if (l2 == 0) {
      cache[l1][l2] = l1;
      return cache[l1][l2];
    }
    if (s1.charAt(l1 - 1) == s2.charAt(l2 - 1)) {
      cache[l1][l2] = distanceWithCache(s1, s2, l1 - 1, l2 - 1, cache);
      return cache[l1][l2];
    }
    cache[l1][l2] = 1 + Collections.min(List.of(distanceWithCache(s1, s2, l1, l2 - 1, cache),
        distanceWithCache(s1, s2, l1 - 1, l2, cache),
        distanceWithCache(s1, s2, l1 - 1, l2 - 1, cache)));
    return cache[l1][l2];
  }

  /**
   * Compute the levenshtein distance between strings.
   * 
   * @param s1 The first string
   * @param s2 The second string
   * @return String distance
   */
  public static int distance(String s1, String s2) {
    int l1 = s1.length();
    int l2 = s2.length();
    Integer[][] cache = new Integer[l1 + 1][l2 + 1];
    return Levenshtein.distanceWithCache(s1, s2, l1, l2, cache);
  }
}
