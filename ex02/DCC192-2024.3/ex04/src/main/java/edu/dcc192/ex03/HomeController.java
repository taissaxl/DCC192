package edu.dcc192.ex03;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes("usuario")
public class HomeController {

    @Autowired
    private GeradorSenha geradorSenha;

    @Autowired
    private UsuarioRepository ur;

    private String captchaAtual;
    private JSONArray records = new JSONArray();
    private String filePath = "./src/main/resources/loginRecords.json";  // Arquivo JSON para armazenar os logins

    // Carrega o arquivo JSON ou cria um novo, se não existir
    public void loadLoginRecords() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            JSONParser parser = new JSONParser();
                records = (JSONArray) parser.parse(new String(bytes));
            } catch (org.json.simple.parser.ParseException e) {
                System.out.println("Erro ao parsear o arquivo JSON.");
                records = new JSONArray();  // Inicializa uma lista vazia em caso de erro de parsing
        } catch (IOException e) {
            System.out.println("Erro ao carregar o arquivo JSON, criando novo arquivo.");
            records = new JSONArray();  // Caso o arquivo não exista, inicializa uma lista vazia
        }
    }

    public void saveLoginRecords() {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(records.toString());  // Escreve no arquivo JSON
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo JSON.");
        }
    }

    // Retorna a data no formato dd/MM
    public String todayDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        return currentDate.format(formatter);
    }

    // Rota principal (Login)
    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView("login");
        captchaAtual = geradorSenha.GerarSenha(); 
        mv.addObject("senha", captchaAtual);
        return mv;
    }

    // Rota para redirecionar para o login com o CAPTCHA escolhido
    @GetMapping("/login-with-captcha")
    public ModelAndView loginWithCaptcha(@RequestParam String captchaOption) {
        ModelAndView mv = new ModelAndView("login");
        captchaAtual = captchaOption; // Define o CAPTCHA escolhido como o atual
        mv.addObject("senha", captchaAtual); // Passa o CAPTCHA escolhido para a página de login
        return mv;
    }

    // Verificação do CAPTCHA e login
    @SuppressWarnings("unchecked")
    @PostMapping("/login")
    public ModelAndView login(@RequestParam String name, @RequestParam String senha, @RequestParam String captchaInput, Model model) {
        ModelAndView mv = new ModelAndView();

        // Verifica o CAPTCHA
        if (!captchaInput.equals(captchaAtual)) {
            mv.setViewName("login");
            mv.addObject("error", "CAPTCHA incorreto. Tente novamente.");
            captchaAtual = geradorSenha.GerarSenha(); // Gera um novo CAPTCHA
            mv.addObject("senha", captchaAtual); // Passa o novo CAPTCHA para a página
            return mv;
        }

        // Verifica se o usuário existe no banco de dados
        Usuario usuario = ur.findByNomeAndSenha(name, senha);

        if (usuario == null) {
            // Se o usuário não for encontrado, exibe uma mensagem de erro
            mv.setViewName("login");
            mv.addObject("error", "Nome ou senha incorretos! Tente novamente.");
            captchaAtual = geradorSenha.GerarSenha(); // Gera um novo CAPTCHA
            mv.addObject("senha", captchaAtual); // Passa o novo CAPTCHA para a página
            return mv;
        }

        // Se o usuário for encontrado, procede com o login
        Map<String, String> usuarioMap = new HashMap<>();
        usuarioMap.put("nome", usuario.getNome());
        usuarioMap.put("senha", usuario.getSenha());

        model.addAttribute("usuario", usuarioMap);

        // Registra o login no JSON
        loadLoginRecords();
        String today = todayDate();

        // Verifica se já existe o nome do usuário no JSON
        boolean userExists = false;
        for (int i = 0; i < records.size(); i++) {
            JSONObject userRecord = (JSONObject) records.get(i);
            if (((String) userRecord.get("nome")).equals(name)) {
                userExists = true;
                // Verifica se é o mesmo dia
                if (userRecord.containsKey(today)) {
                    int logins = ((Long) userRecord.get(today)).intValue() + 1;
                    userRecord.put(today, logins);  // Incrementa o login do dia
                } else {
                    userRecord.put(today, 1);  // Se não for o mesmo dia, inicia com 1 login
                }
                break;
            }
        }

        // Se o usuário não foi encontrado, adiciona um novo registro
        if (!userExists) {
            JSONObject newUser = new JSONObject();
            newUser.put("nome", name);
            newUser.put(today, 1);  // Primeira vez logando no dia
            records.add(newUser);
        }

        saveLoginRecords();  // Salva as alterações no arquivo JSON

        mv.setViewName("redirect:/index");
        return mv;
    }

    // Página inicial
    @GetMapping("/index")
    public ModelAndView index(@ModelAttribute("usuario") Map<String, String> usuario) {
        ModelAndView mv = new ModelAndView("index");

        if (usuario != null && usuario.containsKey("nome")) {
            String nomeUsuario = usuario.get("nome");
            mv.addObject("userName", nomeUsuario);
        } else {
            mv.addObject("userName");
        }

        List<String> senhasCaptcha = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            senhasCaptcha.add(geradorSenha.GerarSenha());
        }
        mv.addObject("captchaOptions", senhasCaptcha);

        return mv;
    }

    // Logout
    @GetMapping("/logout")
    public ModelAndView getMethodLogout() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");
        mv.addObject("logout", true);
        return mv;
    }

    // Página Bootstrap
    @GetMapping("/bootstrap")
    public ModelAndView bootstrap() {
        return new ModelAndView("bootstrap.html");
    }

    // Página de Informações
    @GetMapping("/info")
    public ModelAndView info() {
        return new ModelAndView("info.html");
    }

    // Exibe o formulário para adicionar um novo usuário
    @GetMapping("/novo-usuario")
    public ModelAndView novoUsuario() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("form.html");
        mv.addObject("usuario", new Usuario());
        return mv;
    }

    // Salva o usuário e redireciona para a página de login
    @PostMapping("/salvar-usuario")
    public String salvarUsuario(@ModelAttribute Usuario usuario) {
        ur.save(usuario); // Salva o usuário no banco de dados
        return "redirect:/"; // Redireciona para a página de login
    }

    // Lista de usuários
    @GetMapping("/usuarios")
    public ModelAndView listarUsuarios() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("usuarios");
        List<Usuario> usuarios = ur.findAll();
        mv.addObject("usuarios", usuarios);
        return mv;
    }

    // Rota para gerar um erro 404
    @GetMapping("/erro-404")
    public String erro404() throws NoHandlerFoundException {
        throw new NoHandlerFoundException("GET", "/erro.html", null);
    }

    // Rota para gerar um erro de Java
    @GetMapping("/erro-java")
    public String erroJava() {
        throw new RuntimeException("Erro no tratamento de dados");
    }

    // Dashboard de logins
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        loadLoginRecords();

        // Criando uma lista de mapas com os dados formatados
        List<Map<String, Object>> formattedData = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            JSONObject userRecord = (JSONObject) records.get(i);
            Map<String, Object> record = new HashMap<>();
            
            // Nome do usuário
            String nome = userRecord.get("nome").toString();
            
            // Map para armazenar contagens de login por data
            Map<String, Integer> loginCounts = new HashMap<>();
            
            // Adicionando as contagens de login para cada data (exceto o nome)
            for (Object dateObj : userRecord.keySet()) {
                String date = (String) dateObj;
                if (!"nome".equals(date)) {
                    loginCounts.put(date, ((Long) userRecord.get(date)).intValue());
                }
            }
            
            // Adiciona o nome e os logins ao mapa de dados
            record.put("nome", nome);
            record.put("logins", loginCounts);
            
            // Adiciona o registro formatado à lista
            formattedData.add(record);
        }

        // Passa a lista de dados formatados para o modelo
        model.addAttribute("loginData", formattedData);
        
        // Retorna a página do dashboard
        return "dashboard";  // Página do dashboard
    }
}