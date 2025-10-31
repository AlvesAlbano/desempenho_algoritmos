import java.io.*;
import java.util.*;

public class LeitorArquivo {
    private String caminho;

    public LeitorArquivo(String caminho) {
        this.caminho = caminho;
    }

    public int[] ler() {
        List<Integer> numeros = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(caminho))) {
            String linha;

            while ((linha = bufferedReader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    numeros.add(Integer.parseInt(linha.trim()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Converte List<Integer> para int[]
        int[] resultado = new int[numeros.size()];
        for (int i = 0; i < numeros.size(); i++) {
            resultado[i] = numeros.get(i);
        }

        return resultado;
    }
}
