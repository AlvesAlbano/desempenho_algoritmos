package algoritmos;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class CountingSort {

    public static void sortSerial(int[] lista) {

        int n = lista.length;

        int maiorValor = maiorValor(lista);

        int[] listaContagem = new int[maiorValor + 1];
        for (int i = 0; i <= maiorValor; i++) {
            listaContagem[i] = 0;
        }

        contarFrequencia(lista, listaContagem);

        prefixSum(listaContagem, maiorValor);

        int[] listaFinal = new int[n];
        for (int i = n - 1; i >= 0; i--) {
            int v = lista[i];
            
            listaFinal[listaContagem[v] - 1] = v;
            listaContagem[v]--;
        }
        
    }
    
    public static void sortParalelo(int[] lista, int quantidadeThreads){

        try (ExecutorService executorService = Executors.newFixedThreadPool(quantidadeThreads)) {
            
            int n = lista.length;
            int maiorValor = maiorValor(lista);

            int[][] listaContagemLocal = new int[quantidadeThreads][maiorValor + 1];
            
            int chunckSize = (n + quantidadeThreads - 1) / quantidadeThreads;

            for (int t = 0; t < quantidadeThreads; t++) {
                int inicio = t * chunckSize;
                int fim = Math.min(inicio + chunckSize,n);
                int threadIdx = t;

                executorService.submit(() -> {
                    
                    for (int i = inicio; i < fim; i++) {
                        listaContagemLocal[threadIdx][lista[i]]++;
                    }
                });
            }

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);


            int[] listaContagem = new int[maiorValor + 1];
            
            for (int i = 0; i <= maiorValor; i++) {
                for (int k = 0; k < quantidadeThreads; k++) {
                    listaContagem[i] += listaContagemLocal[k][i];
                }
            }
            
            PrefixSumTarefa.prefixSumParalelo(listaContagem,quantidadeThreads);
            
            int[] listaFinal = new int[n];
            
            for (int i = n - 1; i >= 0; i--) {
                int v = lista[i];
                listaFinal[listaContagem[v] - 1] = v;
                listaContagem[v]--;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static int maiorValor(int[] lista){
        int maiorValor = lista[0];

        for (int i = 1; i < lista.length; i++) {
            if (lista[i] > maiorValor){
                maiorValor = lista[i];
            }
        }

        return maiorValor;
    }

    private static void prefixSum(int[] listaContagem, int maiorValor){
        for (int i = 1; i <= maiorValor; i++) {
            listaContagem[i] += listaContagem[i - 1];
        }
    }

    private static void contarFrequencia(int[] lista, int[] listaContagem){
        for (int i = 0; i < lista.length; i++) {
            listaContagem[lista[i]]++;
        }
    }

    private static class PrefixSumTarefa extends RecursiveAction{
        
        private int[] lista;
        private int inicio;
        private int fim;
        private int tamanhoTarefa;
        
        public PrefixSumTarefa(int[] lista, int inicio, int fim, int tamanhoTarefa) {
            this.lista = lista;
            this.inicio = inicio;
            this.fim = fim;
            this.tamanhoTarefa = tamanhoTarefa;
        }

        @Override
        protected void compute() {
            
            if (fim - inicio <= tamanhoTarefa){
            // System.out.println("serialfds");
                for (int i = inicio + 1; i < fim; i++) {
                    lista[i] += lista[i-1];
                }
            } else {

                int meio = (inicio + fim) / 2;
        
                PrefixSumTarefa esquerda = new PrefixSumTarefa(lista, inicio, meio, tamanhoTarefa);
                PrefixSumTarefa direita = new PrefixSumTarefa(lista, meio, fim, tamanhoTarefa);
    
                invokeAll(esquerda,direita);
    
                int offset = lista[meio-1];
    
                for (int i = meio; i < fim; i++) {
                    lista[i] += offset;
                }
            }
        }
        
        public static void prefixSumParalelo(int[] lista, int quantidadeThreads){

            final int TAREFAS_POR_THREADS = 4;
            final int n = lista.length;
            
            int tamanhoTarefa = Math.max(n / (quantidadeThreads * TAREFAS_POR_THREADS), 1);
            
            ForkJoinPool.commonPool().invoke(new PrefixSumTarefa(lista, 0, n - 1,tamanhoTarefa));
        }
    }
}