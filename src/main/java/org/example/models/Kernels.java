package org.example.models;

public class Kernels {

    /**
     * Genera un kernel Gaussiano de tamaño variable.
     * Es útil para aplicar desenfoque a una imagen.
     *
     * @param size  Tamaño del kernel (debe ser impar).
     * @param sigma Desviación estándar de la distribución Gaussiana.
     * @return Kernel Gaussiano normalizado.
     * @throws IllegalArgumentException si el tamaño no es impar.
     */
    public static float[][] generateGaussianKernel(int size, float sigma) {
        if (size % 2 == 0) throw new IllegalArgumentException("Tamaño impar requerido");

        float[][] kernel = new float[size][size];
        int center = size / 2;
        float sum = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = i - center;
                int y = j - center;
                float value = (float) (Math.exp(-(x * x + y * y) / (2 * sigma * sigma)));
                kernel[i][j] = value;
                sum += value;
            }
        }

        // Normalizar el kernel para que la suma total sea 1
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                kernel[i][j] /= sum;

        return kernel;
    }

    /**
     * Genera un kernel Sobel en dirección X de tamaño variable.
     * Se utiliza para detectar bordes verticales.
     *
     * @param size Tamaño del kernel (debe ser impar).
     * @return Kernel Sobel X.
     */
    public static float[][] generateSobelXKernel(int size) {
        float[][] kernel = new float[size][size];
        int center = size / 2;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] = (j - center); // Cambia a valores negativos a la izquierda y positivos a la derecha
            }
        }

        return kernel;
    }

    /**
     * Genera un kernel Laplaciano de tamaño variable.
     * Se utiliza para detectar bordes (resalta cambios bruscos de intensidad).
     *
     * @param size Tamaño del kernel (debe ser impar).
     * @return Kernel Laplaciano.
     */
    public static float[][] generateLaplacianKernel(int size) {
        float[][] kernel = new float[size][size];
        int center = size / 2;

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                kernel[i][j] = -1;

        kernel[center][center] = size * size - 1; // El centro tiene un valor positivo mayor
        return kernel;
    }
}
