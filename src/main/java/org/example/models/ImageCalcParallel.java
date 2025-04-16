package org.example.models;

import java.awt.image.BufferedImage;


/**
 * Clase que extiende Thread para realizar procesamiento de imágenes en paralelo.
 * Se encarga de aplicar un filtro de convolución a una subregión de la imagen de entrada.
 */
public class ImageCalcParallel extends Thread {
    private final BufferedImage image;
    private final BufferedImage resultImage;
    private final float[][] kernel;
    private final int startX, endX;
    private final int width, height, sizeKernel, margin;

    /**
     * Constructor de la clase ImageCalcParallel.
     * Inicializa los parámetros necesarios para aplicar la convolución en paralelo.
     *
     * @param image       Imagen de entrada sobre la que se aplicará el filtro.
     * @param resultImage Imagen de salida donde se almacenará el resultado.
     * @param kernel      Matriz de convolución a aplicar.
     * @param startX      Coordenada X de inicio del bloque de la imagen a procesar (debe ser >= margin).
     * @param endX        Coordenada X de fin del bloque de la imagen a procesar (debe ser <= width - margin).
     * @param width       Ancho de la imagen.
     * @param height      Alto de la imagen.
     */
    public ImageCalcParallel(BufferedImage image, BufferedImage resultImage, float[][] kernel,
                             int startX, int endX, int width, int height) {
        this.image = image;
        this.resultImage = resultImage;
        this.kernel = kernel;
        this.startX = startX;
        this.endX = endX;
        this.width = width;
        this.height = height;
        this.sizeKernel = kernel.length;
        this.margin = sizeKernel / 2;
    }

    /**
     * Método que ejecuta la convolución en paralelo sobre una sección de la imagen.
     * Se recorren los píxeles dentro de la región asignada (sin bordes) y se aplica la operación
     * de convolución usando la matriz del kernel.
     */
    @Override
    public void run() {
        // Se itera en x solamente en el rango seguro definido (startX a endX)
        for (int x = startX; x < endX; x++) {
            // En y se itera de margin a height-margin, igual que en el método secuencial
            for (int y = margin; y < height - margin; y++) {
                float redSum = 0, greenSum = 0, blueSum = 0;

                // Se recorre el kernel
                for (int i = 0; i < sizeKernel; i++) {
                    for (int j = 0; j < sizeKernel; j++) {
                        // Calcular la posición correspondiente en la imagen
                        int pixelX = x + i - margin;
                        int pixelY = y + j - margin;

                        // Aquí no es necesario chequear límites porque startX y endX se configuraron
                        // para que pixelX siempre esté entre margin y width-margin.
                        int pixel = image.getRGB(pixelX, pixelY);

                        int red   = (pixel >> 16) & 0xFF;
                        int green = (pixel >> 8)  & 0xFF;
                        int blue  = pixel & 0xFF;

                        redSum   += red   * kernel[i][j];
                        greenSum += green * kernel[i][j];
                        blueSum  += blue  * kernel[i][j];
                    }
                }

                // Se asegura que los valores estén en el rango [0,255]
                int newRed   = Math.min(Math.max((int) redSum, 0), 255);
                int newGreen = Math.min(Math.max((int) greenSum, 0), 255);
                int newBlue  = Math.min(Math.max((int) blueSum, 0), 255);

                int newPixel = (newRed << 16) | (newGreen << 8) | newBlue;
                resultImage.setRGB(x, y, newPixel);
            }
        }
    }
}
