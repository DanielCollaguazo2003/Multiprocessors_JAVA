package org.example;

import org.example.models.Imagen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        float[][] blur = {
                {0,1,  0,1,  0,1},
                {0,1,  0,1,  0,1},
                {0,1,  0,1,  0,1}
        };

        float[][] gaussianBlur = {
                {1/16f, 2/16f, 1/16f},
                {2/16f, 4/16f, 2/16f},
                {1/16f, 2/16f, 1/16f}
        };


        float[][] sobelX = {
                {-1,  0,  1},
                {-2,  0,  2},
                {-1,  0,  1}
        };

        float[][] sobelY = {
                {-1, -2, -1},
                { 0,  0,  0},
                { 1,  2,  1}
        };

        float[][] prewittX = {
                {-1,  0,  1},
                {-1,  0,  1},
                {-1,  0,  1}
        };


        float[][] prewittY = {
                {-1, -1, -1},
                { 0,  0,  0},
                { 1,  1,  1}
        };

        float[][] laplacian = {
                { 0, -1,  0},
                {-1,  4, -1},
                { 0, -1,  0}
        };

        float[][] sharpen = {
                {  0, -1,  0},
                { -1,  5, -1},
                {  0, -1,  0}
        };

        float[][] emboss = {
                {-2, -1,  0},
                {-1,  1,  1},
                { 0,  1,  2}
        };

        File file = new File("src/main/resources/ej3.jpg"); // Ruta de la imagen
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Imagen cargada con éxito: " + file.getName());

        Imagen imagen = new Imagen();


        //Ejecucion Secuencial
        long startTime1 = System.nanoTime();
        BufferedImage resultImage1 = imagen.applyConvolutionRGB(image, emboss);
        long endTime1 = System.nanoTime();
        long duration1 = endTime1 - startTime1;
        System.out.println("Tiempo de ejecución: " + duration1 / 1_000_000 + " ms");
        imagen.saveImage(resultImage1, "src/main/resources/imageSecuentialEj3.jpg");


        //Ejecucion Parallela
        long startTime2 = System.nanoTime();
        BufferedImage resultImage2 = imagen.applyConvolutionParallelRGB(image, emboss);
        long endTime2 = System.nanoTime();
        long duration2 = endTime2 - startTime2;
        System.out.println("Tiempo de ejecución: " + duration2 / 1_000_000 + " ms");
        imagen.saveImage(resultImage2, "src/main/resources/imageParallelEj3.jpg");
    }
}