package org.example.models;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


/**
 * Clase para manipulación de imágenes, incluyendo la aplicación de filtros de convolución
 * en imágenes RGB, tanto en forma secuencial como en paralelo.
 */
public class Imagen {
    BufferedImage image;

    /**
     * Guarda una imagen en un archivo especificado.
     *
     * @param image      Imagen a guardar.
     * @param outputPath Ruta de salida donde se almacenará la imagen.
     */
    public void saveImage(BufferedImage image, String outputPath) {
        try {
            File output = new File(outputPath);
            ImageIO.write(image, "png", output);
            System.out.println("Imagen guardada en: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
        }
    }

    /**
     * Aplica una convolución a una imagen RGB utilizando un filtro definido por un kernel.
     * La operación se realiza de forma secuencial.
     *
     * @param image  Imagen de entrada.
     * @param kernel Matriz del filtro a aplicar.
     * @return Imagen resultante después de aplicar la convolución.
     */
    public BufferedImage applyConvolutionRGB(BufferedImage image, float[][] kernel) {
        int width = image.getWidth();
        int height = image.getHeight();
        int sizeKernel = kernel.length;
        int margin = sizeKernel / 2;

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = margin; x < width - margin; x++) {
            for (int y = margin; y < height - margin; y++) {

                float redSum = 0, greenSum = 0, blueSum = 0;

                for (int i = 0; i < sizeKernel; i++) {
                    for (int j = 0; j < sizeKernel; j++) {
                        int pixel = image.getRGB(x + i - margin, y + j - margin);

                        int red = (pixel >> 16) & 0xFF;
                        int green = (pixel >> 8) & 0xFF;
                        int blue = pixel & 0xFF;

                        redSum += red * kernel[i][j];
                        greenSum += green * kernel[i][j];
                        blueSum += blue * kernel[i][j];
                    }
                }

                int newRed = Math.min(Math.max((int) redSum, 0), 255);
                int newGreen = Math.min(Math.max((int) greenSum, 0), 255);
                int newBlue = Math.min(Math.max((int) blueSum, 0), 255);

                int newPixel = (newRed << 16) | (newGreen << 8) | newBlue;
                resultImage.setRGB(x, y, newPixel);
            }
        }

        return resultImage;
    }

    /**
     * Aplica una convolución a una imagen RGB utilizando un filtro definido por un kernel.
     * La operación se realiza de forma paralela, dividiendo la imagen en bloques según el
     * número de núcleos disponibles.
     *
     * @param image  Imagen de entrada.
     * @param kernel Matriz del filtro a aplicar.
     * @return Imagen resultante después de aplicar la convolución en paralelo.
     */
    public BufferedImage applyConvolutionParallelRGB(BufferedImage image, float[][] kernel) {
        int width = image.getWidth();
        int height = image.getHeight();
        int sizeKernel = kernel.length;
        int margin = sizeKernel / 2;

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int numThreads = Runtime.getRuntime().availableProcessors();
        int blockWidth = (width - 2 * margin) / numThreads;
        int remainder = (width - 2 * margin) % numThreads;

        ImageCalcParallel[] threads = new ImageCalcParallel[numThreads];
        int startX = 0;

        for (int i = 0; i < numThreads; i++) {
            int endX = startX + blockWidth + (i == numThreads - 1 ? remainder : 0);

            endX = Math.min(endX, width - margin);

            threads[i] = new ImageCalcParallel(image, resultImage, kernel, startX, endX, width, height);
            threads[i].start();

            startX = endX;
        }

        try {
            for (ImageCalcParallel t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultImage;
    }
}