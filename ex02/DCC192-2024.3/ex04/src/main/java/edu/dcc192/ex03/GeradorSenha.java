package edu.dcc192.ex03;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class GeradorSenha { 
    
    private static final int TAMANHO_SENHA_RANDOMICA = 4;
    private static int seed = 0;
    private final Random random = new Random();

    public String GerarSenha(){
        
        String senha = "";
        for(int count = 0; count < GeradorSenha.TAMANHO_SENHA_RANDOMICA/2; count++)
        {
            senha += (char)(((int)'0')+(Math.abs(random.nextInt()+GeradorSenha.seed)%('9'-'0'+1)));
            senha += (char)(((int)'a')+(Math.abs(random.nextInt()+GeradorSenha.seed)%('z'-'a'+1)));
        }
        GeradorSenha.seed += 10;
        return senha;
    }
}
