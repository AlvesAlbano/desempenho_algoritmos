package algoritmos;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class InsertionSort {

    public static void sortSerial(int[] lista) {
        int n = lista.length;

        for (int i = 1; i < n; ++i) {
            int key = lista[i];
            int j = i - 1;

            while (j >= 0 && lista[j] > key) {
                lista[j + 1] = lista[j];
                j = j - 1;
            }
            lista[j + 1] = key;
        }
    }

    public static void sortParalelo(int[] lista, int quantidadeThreads){
        try (ExecutorService executorService = Executors.newFixedThreadPool(quantidadeThreads)) {
            int n = lista.length;
            int tamanhoSublistas = (int) Math.ceil((double) n / quantidadeThreads);
            
            Future<int[]>[] future = new Future[quantidadeThreads];

            for (int i = 0; i < quantidadeThreads; i++) {
                int inicio = i * tamanhoSublistas;
                int fim = Math.min(inicio + tamanhoSublistas, n);
                if (inicio >= fim) break;
                

                int[] subLista = Arrays.copyOfRange(lista,inicio,fim);

                future[i] = executorService.submit(() -> {
                    sortSerial(subLista);
                    return subLista;
                });

            }

            int[][] subListasOrdenadas = new int[quantidadeThreads][];
            
            for (int i = 0; i < quantidadeThreads; i++) {
                if (future[i] != null){
                    subListasOrdenadas[i] = future[i].get(); 
                }
            }

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
            
            int[] listaFinal = subListasOrdenadas[0];
            
            for (int i = 0; i < subListasOrdenadas.length; i++) {
                if (subListasOrdenadas[i] != null){
                    listaFinal = juntarSubListas(listaFinal, subListasOrdenadas[i]);
                }
            }
        
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    private static int[] juntarSubListas(int[] a, int[] b) {
        int[] resultado = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;

        while (i < a.length && j < b.length) {
            if (a[i] < b[j]) {
                resultado[k++] = a[i++];
            } else {
                resultado[k++] = b[j++];
            }
        }

        while (i < a.length) {
            resultado[k++] = a[i++];
        }

        while (j < b.length) {
            resultado[k++] = b[j++];
        }

        return resultado;
    }
}