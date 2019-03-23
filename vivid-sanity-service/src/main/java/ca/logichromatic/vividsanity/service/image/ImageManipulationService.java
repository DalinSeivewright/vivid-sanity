package ca.logichromatic.vividsanity.service.image;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageManipulationService {
    private static Comparator<Pair<Color, Integer>> countComparator = Comparator.comparing((Pair<Color, Integer> colorPair) -> colorPair.getRight());
    private static Comparator<Pair<Color, Integer>> redComparator = Comparator.comparing((Pair<Color, Integer> colorPair) -> colorPair.getLeft().getRed());
    private static Comparator<Pair<Color, Integer>> greenComparator = Comparator.comparing((Pair<Color, Integer> colorPair) -> colorPair.getLeft().getGreen());
    private static Comparator<Pair<Color, Integer>> blueComparator = Comparator.comparing((Pair<Color, Integer> colorPair) -> colorPair.getLeft().getBlue());

    public BufferedImage getImage(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }

    // Note:  I have messed around with many settings in an attempt to get this to mimic as
    // as closely as possible to the image you'd get from the python PIL image library
    // This is necessary to get a decent palette generated
    public BufferedImage getThumbnail(BufferedImage originalImage) {
        return Scalr.resize(originalImage, Scalr.Method.ULTRA_QUALITY, 256, 256);
    }

    // See License notice at licenses/haishoku
    public List<Color> getPalette(BufferedImage thumbnail) {
        List<Pair<Color, Integer>> colors = getColorsMean(thumbnail);
        return colors.stream().map(Pair::getLeft).collect(Collectors.toList());
    }

    public List<Pair<Color, Integer>> getColors(BufferedImage image) {
        Map<Color, Integer> colorsMap = new HashMap<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                if (colorsMap.containsKey(color)) {
                    colorsMap.put(color, colorsMap.get(color) + 1);
                } else {
                    colorsMap.put(color, 1);
                }
            }
        }
        return colorsMap.entrySet().stream()
                .map(colorEntry -> Pair.of(colorEntry.getKey(), colorEntry.getValue()))
                .collect(Collectors.toList());
    }
    
    public List<Pair<Color, Integer>> getColorsMean(BufferedImage thumbnailImage) {
        List<Pair<Color, Integer>> colors = getColors(thumbnailImage);
        List<Pair<Color, Integer>> sortedColors = sortColors(colors);

        List<Pair<Color, Integer>>[][][] groupedColors = groupByAccuracy(sortedColors);
        List< Pair<Color, Integer>> colorMeans = new ArrayList<>();
        for(int i = 0; i < groupedColors.length; i++) {
            for(int j = 0; j < groupedColors.length; j++) {
                for(int k = 0; k < groupedColors.length; k++) {
                    if (groupedColors[i][j][k] != null) {
                        Pair<Color, Integer> colorMean = getWeightedMean(groupedColors[i][j][k]);
                        colorMeans.add(colorMean);
                    }
                }
            }
        }
        List<Pair<Color, Integer>> finalMeans = colorMeans.stream()
                .sorted(countComparator
                        .thenComparing(redComparator)
                        .thenComparing(greenComparator)
                        .thenComparing(blueComparator))
                .collect(Collectors.toList());

        return finalMeans.stream().limit(8).collect(Collectors.toList());
    }

    private Pair<Color, Integer> getWeightedMean(List<Pair<Color, Integer>> colorPairs) {
        int sigmaCount = 0;
        int sigmaRed = 0;
        int sigmaGreen = 0;
        int sigmaBlue = 0;
        for(Pair<Color, Integer> color : colorPairs) {
            sigmaCount += color.getRight();
            sigmaRed += color.getLeft().getRed() * color.getRight();
            sigmaGreen += color.getLeft().getGreen() * color.getRight();
            sigmaBlue += color.getLeft().getBlue() * color.getRight();

        }
        int redWeightedMean = (sigmaRed / sigmaCount);
        int greenWeightedMean = (sigmaGreen / sigmaCount);
        int blueWeightedMean = (sigmaBlue / sigmaCount);

        return Pair.of(new Color(redWeightedMean, greenWeightedMean, blueWeightedMean), sigmaCount);
    }

    private List<Pair<Color, Integer>> sortColors(List<Pair<Color, Integer>> colors) {
        Comparator<Pair<Color, Integer>> colorPairComparator= redComparator.thenComparing(greenComparator).thenComparing(blueComparator);
        return colors.stream().sorted(colorPairComparator).collect(Collectors.toList());
    }

    private List<Pair<Color, Integer>>[][][] groupByAccuracy(List<Pair<Color, Integer>> colors) {
        RgbMinMaxMidRange rgbMinMaxMidRange = getRgbMinMax(colors);
        List<Pair<Color, Integer>>[][][] groupedByAccuracy = createGroupedAccuracyLists();

        colors.stream().forEach((colorPair) -> {

            int redTempValue = colorPair.getLeft().getRed();
            int greenTempValue = colorPair.getLeft().getGreen();
            int blueTempValue = colorPair.getLeft().getBlue();

            int redIndex = calcIndex(redTempValue, rgbMinMaxMidRange.getMinRedValue(), rgbMinMaxMidRange.getMidRedValue());
            int greenIndex = calcIndex(greenTempValue, rgbMinMaxMidRange.getMinGreenValue(), rgbMinMaxMidRange.getMidGreenValue());
            int blueIndex = calcIndex(blueTempValue, rgbMinMaxMidRange.getMinBlueValue(), rgbMinMaxMidRange.getMidBlueValue());
            if (groupedByAccuracy[redIndex][greenIndex][blueIndex] == null) {
                groupedByAccuracy[redIndex][greenIndex][blueIndex] = new ArrayList<>();
            }
            groupedByAccuracy[redIndex][greenIndex][blueIndex].add(colorPair);
        });
        return groupedByAccuracy;
    }

    private List<Pair<Color, Integer>>[][][] createGroupedAccuracyLists() {
        return (List<Pair<Color, Integer>>[][][])Array.newInstance(List.class, 3, 3, 3);
    }

    private int calcIndex(int redTempValue, int minRedValue, float midRedValue) {
        int index = 2;
        if (redTempValue < (minRedValue+midRedValue)) {
            index = 0;
        } else if (redTempValue < minRedValue+midRedValue * 2) {
            index = 1;
        }
        return index;
    }

    private RgbMinMaxMidRange getRgbMinMax(List<Pair<Color, Integer>> colors) {
        List<Pair<Color, Integer>> redSortedColors = colors.stream().sorted(redComparator).collect(Collectors.toList());
        List<Pair<Color, Integer>> greenSortedColors = colors.stream().sorted(greenComparator).collect(Collectors.toList());
        List<Pair<Color, Integer>> blueSortedColors = colors.stream().sorted(blueComparator).collect(Collectors.toList());
        int minRedValue = redSortedColors.get(0).getKey().getRed();
        int minGreenValue = greenSortedColors.get(0).getKey().getGreen();
        int minBlueValue = blueSortedColors.get(0).getKey().getBlue();

        int maxRedValue = redSortedColors.get(redSortedColors.size()-1).getKey().getRed();
        int maxGreenValue = greenSortedColors.get(greenSortedColors.size()-1).getKey().getGreen();
        int maxBlueValue = blueSortedColors.get(blueSortedColors.size()-1).getKey().getBlue();

        return new RgbMinMaxMidRange()
                .setMinRedValue(minRedValue)
                .setMaxRedValue(maxRedValue)
                .setMinGreenValue(minGreenValue)
                .setMaxGreenValue(maxGreenValue)
                .setMinBlueValue(minBlueValue)
                .setMaxBlueValue(maxBlueValue)
                .setMidRedValue((float)(maxRedValue - minRedValue) / 3.0f)
                .setMidGreenValue((float)(maxGreenValue - minGreenValue) / 3.0f)
                .setMidBlueValue((float)(maxBlueValue - minBlueValue) / 3.0f);
    }

    @Data
    @Accessors(chain = true)
    private class RgbMinMaxMidRange {
        private int minRedValue;
        private int maxRedValue;

        private int minGreenValue;
        private int maxGreenValue;

        private int minBlueValue;
        private int maxBlueValue;

        private float midRedValue;
        private float midGreenValue;
        private float midBlueValue;
    }

}
