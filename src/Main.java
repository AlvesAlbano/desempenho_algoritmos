import java.util.Arrays;
import algoritmos.CountingSort;
import algoritmos.InsertionSort;
import algoritmos.MergeSort;
import algoritmos.QuickSort;

public class Main {

    public static void main(String[] args) throws Exception {
        
        String[] caminhos = new String[] {
            "src/dados/dados5000.txt", // 5_000
            "src/dados/dados50000.txt", // 50_000
            "src/dados/dados100000.txt", // 100_000
            "src/dados/dados500000.txt", // 500_000
            "src/dados/dados1000000.txt", // 1_000_000
        };

        final int iteracoes = 10;
        final int quantidadeThreads = 4;
        // final int quantidadeThreads = Runtime.getRuntime().availableProcessors();
        // System.out.println(quantidadeThreads);
        final boolean ehSerial = false;
        
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
}
