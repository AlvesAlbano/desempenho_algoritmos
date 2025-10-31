package algoritmos;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class QuickSort {
    
    public static void sortSerial(int[] lista) {

        int inicio = 0;
        int fim = lista.length - 1;
        sortSerial(lista, inicio, fim);
    }

    private static int partition(int[] lista, int inicio, int fim) {
        int pivoIdx = inicio + (int) (Math.random() * (fim - inicio + 1));
        int pivo = lista[pivoIdx];

        swap(lista, pivoIdx, fim);

        int i = inicio - 1;
        for (int j = inicio; j < fim; j++) { 
            if (lista[j] < pivo) {
                i++;
                swap(lista, i, j);
            }
        }

        swap(lista, i + 1, fim);
        return i + 1;
    }

    private static void swap(int[] lista, int i, int j) {
        int temp = lista[i];
        lista[i] = lista[j];
        lista[j] = temp;
    }

    private static void sortSerial(int[] lista, int inicio, int fim) {
        if (inicio < fim) {
            int pi = partition(lista, inicio, fim);
            sortSerial(lista, inicio, pi - 1);
            sortSerial(lista, pi + 1, fim);
        }
    }

    public static void sortParalelo(int[] lista, int quantidadeThreads) {
        try (ForkJoinPool forkJoinPool = new ForkJoinPool(quantidadeThreads)) {
            int tamanhoLista = lista.length;
            int inicio = 0;
            int fim = tamanhoLista - 1;

            final int FATOR_DIVISAO_PARALELA = 4;
            final int LIMITE_MINIMO_SUBLISTAS = Math.max(tamanhoLista / (quantidadeThreads * FATOR_DIVISAO_PARALELA), 10000);

            forkJoinPool.invoke(new QuickSortTask(lista, inicio, fim, LIMITE_MINIMO_SUBLISTAS));
            forkJoinPool.shutdown();
        }
    }

    private static class QuickSortTask extends RecursiveAction {
        private int[] lista;
        private int inicio;
        private int fim;
        private final int LIMITE_MINIMO_SUBLISTAS;

        public QuickSortTask(int[] lista, int inicio, int fim, int LIMITE_MINIMO_SUBLISTAS) {
            this.lista = lista;
            this.inicio = inicio;
            this.fim = fim;
            this.LIMITE_MINIMO_SUBLISTAS = LIMITE_MINIMO_SUBLISTAS;
        }

        @Override
        protected void compute() {
            if (fim - inicio < LIMITE_MINIMO_SUBLISTAS) {
                sortSerial(lista, inicio, fim);
            } else {
                int pi = partition(lista, inicio, fim);

                QuickSortTask esquerda = new QuickSortTask(lista, inicio, pi - 1, LIMITE_MINIMO_SUBLISTAS);
                QuickSortTask direita = new QuickSortTask(lista, pi + 1, fim, LIMITE_MINIMO_SUBLISTAS);

                invokeAll(esquerda, direita);
            }
        }
    }
}
