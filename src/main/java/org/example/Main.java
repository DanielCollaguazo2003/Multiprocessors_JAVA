package org.example;

import org.example.models.Imagen;
import org.example.models.Kernels;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        float[][] gaussianKernel = Kernels.generateGaussianKernel(9, 4.5f);
        float[][] sobelXKernel = Kernels.generateSobelXKernel(9);
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
        long startTime1 = System.nanoTime();
        BufferedImage resultImage1 = imagen.applyConvolutionRGB(image, laplacianKernel);
        long endTime1 = System.nanoTime();
        long duration1 = endTime1 - startTime1;
        System.out.println("Tiempo de ejecución: " + duration1 / 1_000_000 + " ms");
        imagen.saveImage(resultImage1, "ej1.jpg", "laplacian", 21, "Secuencial");


        //Ejecucion Parallela
        long startTime2 = System.nanoTime();
        BufferedImage resultImage2 = imagen.applyConvolutionParallelRGB(image, laplacianKernel);
        long endTime2 = System.nanoTime();
        long duration2 = endTime2 - startTime2;
        System.out.println("Tiempo de ejecución: " + duration2 / 1_000_000 + " ms");
        imagen.saveImage(resultImage2, "ej1.jpg", "laplacian", 21, "Paralela");
    }
}