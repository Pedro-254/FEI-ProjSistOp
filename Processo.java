import java.util.ArrayList;
import java.util.List;

public class Processo {
    String nome;
    int tempo_atual;
    int duracao;
    List<Integer> Lista_IO = new ArrayList<>();
    int chegada; 

    // Construtor com parâmetros
    public Processo(String nome, int duracao, List<Integer> Lista_IO,int chegada) {
        this.nome = nome;
        this.duracao = duracao;
        this.tempo_atual = 0;
        this.Lista_IO = Lista_IO;
        this.chegada = chegada;
    }

    //Confere se existe pausa IO e 
    //caso exista remove essa pausa e retorna true
    public boolean conferirIO(){
        if (Lista_IO.get(0) != null) {
            if (tempo_atual == Lista_IO.get(0)) {
                Lista_IO.remove(0);
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
        
    };

    //Atualiza o processo e retorna se ele finaliza ou nao
    public boolean AtualizarProcesso(){
        tempo_atual++;
        if (tempo_atual == duracao) {
            return false;
        }
        else{
            return true;
        }
    };

    public int getTempo_atual() {
        return duracao - tempo_atual;
    }
    @Override
    public String toString() {
        return nome;
    }

}
