import java.util.ArrayList;
import java.util.List;

public class Processo {
    String nome;
    String cor;
    int tempo_atual;
    int duracao;
    int chegada;
    List<Integer> Lista_IO = new ArrayList<>();
    List<Integer> Inicio = new ArrayList<>();
    List<Integer> Fim = new ArrayList<>();

    // Construtor com parâmetros
    public Processo(String nome, String cor, int duracao, List<Integer> Lista_IO,int chegada) {
        this.nome = nome;
        this.cor = cor;
        this.duracao = duracao;
        this.tempo_atual = 0;
        this.Lista_IO = Lista_IO;
        this.chegada = chegada;
    }

    //Confere se existe pausa IO e 
    //caso exista remove essa pausa e retorna true
    public boolean conferirIO(){
        // System.out.println(Lista_IO);
        if (Lista_IO.get(0) != null) {
            if (tempo_atual == Lista_IO.get(0)) {
                Lista_IO.remove(0);

                //Se a lista esvaziar add valor NULL
                if (Lista_IO.size() == 0) {
                    Lista_IO.add(null);
                }

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

    public void addInicio(int tempo){
        Inicio.add(tempo);
    }

    public void addFim(int tempo){
        Fim.add(tempo);
    }

    public void Intervalo() {
        System.out.println("Inicio: "+Inicio);
        System.out.println("Fim: "+Fim);        
    }

    public int tempoEspera(){
        int tempo = 0;
        for (int i = 0; i < Inicio.size(); i++) {
            if(i==0){
                tempo += Inicio.get(i) - chegada;
            }
            else{
                tempo += Inicio.get(i) - Fim.get(i-1);
            }
        }
        return tempo;
    }
    
    @Override
    public String toString() {
        return nome;
    }



}
