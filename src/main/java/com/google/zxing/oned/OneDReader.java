/*
 * Copyright 2008 ZXing authors
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

package com.google.zxing.oned;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Encapsulates functionality and implementation that is common to all families
 * of one-dimensional barcodes.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public abstract class OneDReader implements Reader {

  private static final int INTEGER_MATH_SHIFT = 8;
  protected static final int PATTERN_MATCH_RESULT_SCALE_FACTOR = 1 << INTEGER_MATH_SHIFT;

  public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
    return decode(image, null);
  }

  // Note that we don't try rotation without the try harder flag, even if rotation was supported.
  public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, FormatException {
    try {
      return doDecode(image, hints);
    } catch (NotFoundException nfe) {
        throw nfe;
    }
  }
  
  private void locateIgnoreArea(Result result, BinaryBitmap image, Hashtable hints) throws ReaderException {

      ResultPoint[] resultPoints = result.getResultPoints();
      if (resultPoints == null || resultPoints.length == 0) {
	  return;
      }

      int width = image.getWidth();
      int height = image.getHeight();
      float minX = width;
      float minY = height;
      float maxX = 0.0f;
      float maxY = 0.0f;
      for (int i = 0; i < resultPoints.length; i++) {
	  ResultPoint point = resultPoints[i];
	  float x = point.getX();
	  float y = point.getY();
	  if (x < minX) {
	      minX = x;
	  }
	  if (y < minY) {
	      minY = y;
	  }
	  if (x > maxX) {
	      maxX = x;
	  }
	  if (y > maxY) {
	      maxY = y;
	  }
      }

      // go down
      for ( int y = (int) maxY; y < height; y++) {
	  
	  BitArray bitRow = null;
	  boolean isWhiteRow = false;
	  int whiteCount = 0;
	  int dataWidth = 0;
	  
	  try {
	      bitRow = image.getBlackMatrix().getRow(y, new BitArray((int) maxX));
	  } catch ( ReaderException ex ) {
	      isWhiteRow = true;
	  }
	  
	  if ( !isWhiteRow ) {
	      dataWidth = (int) maxX - (int) minX;

	      for (int xpos = (int) minX; xpos <= (int) maxX; xpos++ ) {
		  if ( !(bitRow.get(xpos)) ) {
		      whiteCount++;
		  }
	      }
	  }
	  
	  if ( isWhiteRow || (whiteCount > (int) ((float) dataWidth * 0.80f)) ) {
	      Vector<Rectangle2D> ignoreAreas = (Vector<Rectangle2D>) hints.get(DecodeHintType.IGNORE_AREAS);
	      Rectangle2D ignoreArea = new Rectangle((int) minX, (int) maxY, dataWidth, y - (int) maxY);
	      ignoreAreas.add(ignoreArea);
	      break;
	  }
	  
      }
      hints.put(DecodeHintType.SKIP_TO_ROW, new Integer((int) maxY + 1));

  }

  /**
 * 
 */
private void doSomething() {
    // TODO Auto-generated method stub
    
}

private void whiteOutIgnoreAreas(int rowNumber, BitArray row, Vector<Rectangle2D> ignoreAreas) {
      for ( Rectangle2D ignoreArea: ignoreAreas ) {
	  int minY = (int) ignoreArea.getMinY();
	  int maxY = (int) ignoreArea.getMaxY();
	  if ( rowNumber >= minY && rowNumber <= maxY ) {
	      // white out the x area
	      int minX = (int) ignoreArea.getMinX();
	      int maxX = (int) ignoreArea.getMaxX();
	      for ( int i = minX; i <= maxX; i++ ) {
		  // make black white in this area
		  if ( row.get(i) ) {
		      row.flip(i);
		  }
	      }
	  }
      }
  }

  public void reset() {
    // do nothing
  }

  /**
   * We're going to examine rows from the middle outward, searching alternately above and below the
   * middle, and farther out each time. rowStep is the number of rows between each successive
   * attempt above and below the middle. So we'd scan row middle, then middle - rowStep, then
   * middle + rowStep, then middle - (2 * rowStep), etc.
   * rowStep is bigger as the image is taller, but is always at least 1. We've somewhat arbitrarily
   * decided that moving up and down by about 1/16 of the image is pretty good; we try more of the
   * image if "trying harder".
   *
   * @param image The image to decode
   * @param hints Any hints that were requested
   * @return The contents of the decoded barcode
   * @throws NotFoundException Any spontaneous errors which occur
   */
  private Result doDecode(BinaryBitmap image, Hashtable hints) throws NotFoundException {
      int width = image.getWidth();
      int height = image.getHeight();
      BitArray row = new BitArray(width);

      int maxLines = height; // Look at the whole image, not just the center
      int rowNumber = 0;
      for (rowNumber = (Integer) hints.get(DecodeHintType.SKIP_TO_ROW); rowNumber < maxLines; rowNumber += 2) {


	  // Estimate black point for this row and load it:
	  try {
	      row = image.getBlackRow(rowNumber, row);
	      whiteOutIgnoreAreas(rowNumber, row, (Vector<Rectangle2D>)hints.get(DecodeHintType.IGNORE_AREAS));
	  } catch (NotFoundException nfe) {
	      continue;
	  }

	  try {
	      // Look for a barcode
	      Result result = decodeRow(rowNumber, row, hints);
	      // We found our barcode
	      locateIgnoreArea(result, image, hints);
	      return result;
	  } catch (ReaderException re) {
	      // continue -- just couldn't decode this row
	  }
      
      }

      throw NotFoundException.getNotFoundInstance();
      
  }

  /**
   * Records the size of successive runs of white and black pixels in a row, starting at a given point.
   * The values are recorded in the given array, and the number of runs recorded is equal to the size
   * of the array. If the row starts on a white pixel at the given start point, then the first count
   * recorded is the run of white pixels starting from that point; likewise it is the count of a run
   * of black pixels if the row begin on a black pixels at that point.
   *
   * @param row row to count from
   * @param start offset into row to start at
   * @param counters array into which to record counts
   * @throws NotFoundException if counters cannot be filled entirely from row before running out
   *  of pixels
   */
  protected static void recordPattern(BitArray row, int start, int[] counters) throws NotFoundException {
    int numCounters = counters.length;
    for (int i = 0; i < numCounters; i++) {
      counters[i] = 0;
    }
    int end = row.getSize();
    if (start >= end) {
      throw NotFoundException.getNotFoundInstance();
    }
    boolean isWhite = !row.get(start);
    int counterPosition = 0;
    int i = start;
    while (i < end) {
      boolean pixel = row.get(i);
      if (pixel ^ isWhite) { // that is, exactly one is true
        counters[counterPosition]++;
      } else {
        counterPosition++;
        if (counterPosition == numCounters) {
          break;
        } else {
          counters[counterPosition] = 1;
          isWhite = !isWhite;
        }
      }
      i++;
    }
    // If we read fully the last section of pixels and filled up our counters -- or filled
    // the last counter but ran off the side of the image, OK. Otherwise, a problem.
    if (!(counterPosition == numCounters || (counterPosition == numCounters - 1 && i == end))) {
      throw NotFoundException.getNotFoundInstance();
    }
  }

  protected static void recordPatternInReverse(BitArray row, int start, int[] counters)
      throws NotFoundException {
    // This could be more efficient I guess
    int numTransitionsLeft = counters.length;
    boolean last = row.get(start);
    while (start > 0 && numTransitionsLeft >= 0) {
      if (row.get(--start) != last) {
        numTransitionsLeft--;
        last = !last;
      }
    }
    if (numTransitionsLeft >= 0) {
      throw NotFoundException.getNotFoundInstance();
    }
    recordPattern(row, start + 1, counters);
  }

  /**
   * Determines how closely a set of observed counts of runs of black/white values matches a given
   * target pattern. This is reported as the ratio of the total variance from the expected pattern
   * proportions across all pattern elements, to the length of the pattern.
   *
   * @param counters observed counters
   * @param pattern expected pattern
   * @param maxIndividualVariance The most any counter can differ before we give up
   * @return ratio of total variance between counters and pattern compared to total pattern size,
   *  where the ratio has been multiplied by 256. So, 0 means no variance (perfect match); 256 means
   *  the total variance between counters and patterns equals the pattern length, higher values mean
   *  even more variance
   */
  protected static int patternMatchVariance(int[] counters, int[] pattern, int maxIndividualVariance) {
    int numCounters = counters.length;
    int total = 0;
    int patternLength = 0;
    for (int i = 0; i < numCounters; i++) {
      total += counters[i];
      patternLength += pattern[i];
    }
    if (total < patternLength) {
      // If we don't even have one pixel per unit of bar width, assume this is too small
      // to reliably match, so fail:
      return Integer.MAX_VALUE;
    }
    // We're going to fake floating-point math in integers. We just need to use more bits.
    // Scale up patternLength so that intermediate values below like scaledCounter will have
    // more "significant digits"
    int unitBarWidth = (total << INTEGER_MATH_SHIFT) / patternLength;
    maxIndividualVariance = (maxIndividualVariance * unitBarWidth) >> INTEGER_MATH_SHIFT;

    int totalVariance = 0;
    for (int x = 0; x < numCounters; x++) {
      int counter = counters[x] << INTEGER_MATH_SHIFT;
      int scaledPattern = pattern[x] * unitBarWidth;
      int variance = counter > scaledPattern ? counter - scaledPattern : scaledPattern - counter;
      if (variance > maxIndividualVariance) {
        return Integer.MAX_VALUE;
      }
      totalVariance += variance;
    }
    return totalVariance / total;
  }

  /**
   * <p>Attempts to decode a one-dimensional barcode format given a single row of
   * an image.</p>
   *
   * @param rowNumber row number from top of the row
   * @param row the black/white pixel data of the row
   * @param hints decode hints
   * @return {@link Result} containing encoded string and start/end of barcode
   * @throws NotFoundException if an error occurs or barcode cannot be found
   */
  public abstract Result decodeRow(int rowNumber, BitArray row, Hashtable hints)
      throws NotFoundException, ChecksumException, FormatException;

}
