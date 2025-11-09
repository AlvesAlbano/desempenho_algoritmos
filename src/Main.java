import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import algoritmos.CountingSort;
import algoritmos.InsertionSort;
import algoritmos.MergeSort;
import algoritmos.QuickSort;

public class Main {
    private static void gerarGraficos() throws IOException {
        String command = "python graficos.py";

        Process p = Runtime.getRuntime().exec(command);
        
        new Thread(() -> {
            try (var reader = new java.io.BufferedReader( new java.io.InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PYTHON] " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private static void executarAlgoritmos(boolean ehSerial, int quantidadeThreads, int iteracoes){
        
        String[] caminhos = new String[] {
            "src/dados/dados5000.txt", // 5_000
            "src/dados/dados50000.txt", // 50_000
            "src/dados/dados100000.txt", // 100_000
            "src/dados/dados500000.txt", // 500_000
            "src/dados/dados1000000.txt", // 1_000_000
        };

        for (int i = 0; i < caminhos.length; i++) {

            LeitorArquivo arquivo = new LeitorArquivo(caminhos[i]);
    
            int[] lista = arquivo.ler();
    
            long volumeDados = lista.length;
            
            System.out.printf("lendo dados de volume %d\n",volumeDados);
            
            if (ehSerial) {
                Desempenho.gerarRelatorioSerial(() -> MergeSort.sortSerial(Arrays.copyOf(lista, lista.length)), iteracoes, "MergeSort", volumeDados);
                Desempenho.gerarRelatorioSerial(() -> QuickSort.sortSerial(Arrays.copyOf(lista, lista.length)), iteracoes, "QuickSort", volumeDados);
                Desempenho.gerarRelatorioSerial(() -> CountingSort.sortSerial(Arrays.copyOf(lista, lista.length)), iteracoes, "CountingSort", volumeDados);
                Desempenho.gerarRelatorioSerial(() -> InsertionSort.sortSerial(Arrays.copyOf(lista, lista.length)), iteracoes, "InsertionSort", volumeDados);
            } else {
                
                Desempenho.gerarRelatorioParalelo(() -> MergeSort.sortParalelo(Arrays.copyOf(lista, lista.length), quantidadeThreads), iteracoes, "MergeSort", volumeDados, quantidadeThreads);
                Desempenho.gerarRelatorioParalelo(() -> QuickSort.sortParalelo(Arrays.copyOf(lista, lista.length), quantidadeThreads), iteracoes, "QuickSort", volumeDados, quantidadeThreads);
                Desempenho.gerarRelatorioParalelo(() -> CountingSort.sortParalelo(Arrays.copyOf(lista, lista.length), quantidadeThreads), iteracoes, "CountingSort", volumeDados, quantidadeThreads);
                Desempenho.gerarRelatorioParalelo(() -> InsertionSort.sortParalelo(Arrays.copyOf(lista, lista.length), quantidadeThreads), iteracoes, "InsertionSort", volumeDados, quantidadeThreads);
            }
        }
    }

    public static void main(String[] args) throws Exception {
                
        int iteracoes;
        int quantidadeThreads;
        int opcao;
        // final int quantidadeThreads = Runtime.getRuntime().availableProcessors();
        // System.out.println(quantidadeThreads);
        boolean ehSerial;
        
        try (Scanner input = new Scanner(System.in)) {
            do {

                System.out.println("Defina a quantidade de iterações: ");
                
                iteracoes = input.nextInt();
                
                System.out.println("Defina o numero de threads: ");
                
                quantidadeThreads = input.nextInt();

                System.out.println("Defina o tipo de execução:");
                System.out.println("1 - Serial");
                System.out.println("2 - Paralelo");

                opcao = input.nextInt();
                ehSerial = opcao == 1 ? true : false;
                
                executarAlgoritmos(ehSerial, quantidadeThreads, iteracoes);
                gerarGraficos();
            } while (true);
        }
    }
}