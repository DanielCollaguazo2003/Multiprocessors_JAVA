package org.example;

import org.example.models.Imagen;
import org.example.models.Kernels;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {


        float[][] gaussianKernel = Kernels.generateGaussianKernel(21, 4.5f);
        float[][] sobelXKernel = Kernels.generateSobelXKernel(21);
        float[][] laplacianKernel = Kernels.generateLaplacianKernel(21);

        File file = new File("src/main/resources/ej1.jpg"); // Ruta de la imagen
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Imagen cargada con éxito: " + file.getName());

        Imagen imagen = new Imagen();


        //Ejecucion Secuencial
//        long startTime1 = System.nanoTime();
//        BufferedImage resultImage1 = imagen.applyConvolutionRGB(image, laplacianKernel);
//        long endTime1 = System.nanoTime();
//        long duration1 = endTime1 - startTime1;
//        System.out.println("Tiempo de ejecución: " + duration1 / 1_000_000 + " ms");
//        imagen.saveImage(resultImage1, "ej1.jpg", "laplacian", 21, "Secuencial");


        //Ejecucion Parallela
        long startTime2 = System.nanoTime();
        BufferedImage resultImage2 = imagen.applyConvolutionParallelRGB(image, laplacianKernel);
        long endTime2 = System.nanoTime();
        long duration2 = endTime2 - startTime2;
        System.out.println("Tiempo de ejecución: " + duration2 / 1_000_000 + " ms");
        imagen.saveImage(resultImage2, "ej1.jpg", "laplacian", 21, "Paralela");

        String pathSecuencial = "src/main/resources/ej1_laplacian_k21_Secuencial.png";
        String pathParalela = "src/main/resources/ej1_laplacian_k21_Paralela.png";

        BufferedImage imgSecuencial = ImageIO.read(new File(pathSecuencial));
        BufferedImage imgParalela = ImageIO.read(new File(pathParalela));

        int width = imgSecuencial.getWidth();
        int height = imgSecuencial.getHeight();

        if (width != imgParalela.getWidth() || height != imgParalela.getHeight()) {
            System.out.println("Las imágenes tienen diferentes dimensiones.");
            return;
        }

        int totalPixels = width * height;
        int differentPixels = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = imgSecuencial.getRGB(x, y);
                int rgb2 = imgParalela.getRGB(x, y);

                if (rgb1 != rgb2) {
                    differentPixels++;
                    if (differentPixels <= 10) { // Imprime solo las primeras 10 diferencias
                        System.out.printf("Diferencia en píxel (%d, %d): Secuencial=%#08x, Paralelo=%#08x%n", x, y, rgb1, rgb2);
                    }
                }
            }
        }

        double errorPercentage = (differentPixels * 100.0) / totalPixels;

        System.out.println("====================================");
        System.out.println("Total de píxeles: " + totalPixels);
        System.out.println("Píxeles diferentes: " + differentPixels);
        System.out.printf("Porcentaje de error: %.4f %%\n", errorPercentage);
    }
}