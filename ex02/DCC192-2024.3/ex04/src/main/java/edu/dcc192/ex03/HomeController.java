package edu.dcc192.ex03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@Controller
@SessionAttributes("usuario")
public class HomeController {

    @Autowired
    private GeradorSenha geradorSenha;

    @Autowired
    private UsuarioRepository ur;

    private String captchaAtual;

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
}