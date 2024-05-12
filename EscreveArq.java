import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;
import java.io.FileWriter;

public class EscreveArq {
    
    List<String> texto = new ArrayList<>();

    public EscreveArq(List<String> texto) {
        this.texto = texto;
    }

    public void escrever(){
        try {
            FileWriter fw = new FileWriter("saida.txt");
            PrintWriter saida = new PrintWriter(fw);
            for (String string : texto) {
                saida.println(string);
            }
            saida.close();
        } catch (Exception e) {
            System.out.println("Erro ao escrever no arquivo");
        }
    }
}