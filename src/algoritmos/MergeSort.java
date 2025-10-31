package algoritmos;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MergeSort {

    public static void sortSerial(int[] lista){
        int esquerda = 0;
        int direita = lista.length - 1;

        sortSerial(lista, esquerda, direita);
    }

    private static void sortSerial(int[] lista,int esquerda, int direita){
        if (esquerda < direita){
            int meio = esquerda + (direita - esquerda) / 2;

            sortSerial(lista, esquerda, meio);
            sortSerial(lista, meio + 1, direita);
            
            merge(lista, esquerda, meio, direita);
        }
    }

    private static void merge(int lista[], int esquerda, int meio, int direita){
        int esquerdaTamanho = meio - esquerda + 1;
        int direitaTamanho = direita - meio;

        int[] listaEsquerda = new int[esquerdaTamanho];
        int[] listaDireita = new int[direitaTamanho];
    
        for (int i = 0; i < listaEsquerda.length; i++) {
            listaEsquerda[i] = lista[esquerda + i];    
        }

        for (int j = 0; j < listaDireita.length; j++) {
            listaDireita[j] = lista[meio + 1 + j];
        }
        
        int i = 0;
        int j = 0;

        int k = esquerda;

        while (i < esquerdaTamanho && j < direitaTamanho) {
            
            if (listaEsquerda[i] <= listaDireita[j]){
                lista[k] = listaEsquerda[i];
                i++;
            } else {
                lista[k] = listaDireita[j];
                j++;
            }
            k++;
        }
        
        while (i < esquerdaTamanho) {
            lista[k] = listaEsquerda[i];
            i++;
            k++;
        }

        while (j < direitaTamanho) {
            lista[k] = listaDireita[j];
            j++;
            k++;
        }
    }

    public static void sortParalelo(int[] lista, int quantidadeThreads) {
        try (ForkJoinPool forkJoinPool = new ForkJoinPool(quantidadeThreads)) {

            int tamanhoLista = lista.length;
            int inicio = 0;
            int fim = tamanhoLista - 1;

            final int FATOR_DIVISAO_PARALELA = 4;
            final int LIMITE_MINIMO_SUBLISTAS = Math.max(tamanhoLista / (quantidadeThreads * FATOR_DIVISAO_PARALELA), 10000);

            forkJoinPool.invoke(new MergeSortTarefa(lista, inicio, fim, LIMITE_MINIMO_SUBLISTAS));
            forkJoinPool.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MergeSortTarefa extends RecursiveAction {
        private final int[] lista;
        private final int inicio;
        private final int fim;
        private final int LIMITE_MINIMO_SUBLISTAS;

        public MergeSortTarefa(int[] lista, int inicio, int fim, int lIMITE_MINIMO_SUBLISTAS) {
            this.lista = lista;
            this.inicio = inicio;
            this.fim = fim;
            LIMITE_MINIMO_SUBLISTAS = lIMITE_MINIMO_SUBLISTAS;
        }

        @Override
        protected void compute() {
            if (fim - inicio + 1 <= LIMITE_MINIMO_SUBLISTAS) {
                sortSerial(lista, inicio, fim);
                return;
            }

            int meio = (inicio + fim) / 2;
            MergeSortTarefa esquerda = new MergeSortTarefa(lista, inicio, meio, LIMITE_MINIMO_SUBLISTAS);
            MergeSortTarefa direita = new MergeSortTarefa(lista, meio + 1, fim, LIMITE_MINIMO_SUBLISTAS);
            invokeAll(esquerda, direita);
            merge(lista, inicio, meio, fim);
        }
    }
}