import java.util.Arrays;
import java.util.Scanner;

import algoritmos.CountingSort;
import algoritmos.InsertionSort;
import algoritmos.MergeSort;
import algoritmos.QuickSort;

public class teste {
    public static void main(String[] args) {
        
        String[] caminhos = new String[] {
            "src/dados/dados1.txt", // 5_000
            "src/dados/dados2.txt", // 50_000
            "src/dados/dados3.txt", // 100_000
            "src/dados/dados4.txt", // 500_000
            "src/dados/dados5.txt", // 1_000_000
        };

        final int iteracoes = 100;
        final int quantidadeThreads = 4;
        // final int quantidadeThreads = Runtime.getRuntime().availableProcessors();
        // System.out.println(quantidadeThreads);
        boolean ehSerial;
        final Scanner input = new Scanner(System.in);
        int escolha;
        System.out.println("vai");

        do{
            
            for (int i = 0;i < caminhos.length; i++) {
                System.out.printf("%d - %s\n",i,caminhos[i]);
            }

            System.out.println("Escolha o conjunto de dados:");
            escolha = input.nextInt();
            
            LeitorArquivo arquivo = new LeitorArquivo(caminhos[escolha]);
    
            int[] lista = arquivo.ler();
    
            long volumeDados = lista.length;

            System.out.println("Execução Serial ou Paralela dos algoritmos?:");
            
            System.out.println("1. Paralela");
            System.out.println("2. Serial");
            escolha = input.nextInt();

            ehSerial = escolha == 1 ? true:false;

            
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
            

            
        } while(escolha != 0);

        input.close();
    }
}
