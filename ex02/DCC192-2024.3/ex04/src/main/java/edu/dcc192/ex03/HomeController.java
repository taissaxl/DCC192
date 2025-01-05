package edu.dcc192.ex03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("usuario") // Indica que o atributo "usuario" será armazenado na sessão
public class HomeController {

    @Autowired
    private GeradorSenha geradorSenha;

    private String captchaAtual;

    @RequestMapping("/")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView("captcha.html");
        captchaAtual = geradorSenha.GerarSenha(); // Gera a senha do CAPTCHA
        mv.addObject("senha", captchaAtual); // Passa a senha para a página
        return mv;
    }

    @PostMapping("/verify-captcha")
    public ModelAndView verifyCaptcha(@RequestParam String captchaInput) {
        ModelAndView mv = new ModelAndView();
        if (captchaInput.equals(captchaAtual)) {
            mv.setViewName("login.html");
        } else {
            mv.setViewName("captcha.html");
            mv.addObject("error", "CAPTCHA incorreto. Tente novamente.");
            // Gera um novo CAPTCHA
            captchaAtual = geradorSenha.GerarSenha();
            mv.addObject("senha", captchaAtual); // Passa a nova senha para a página
        }
        return mv;
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String name, Model model) {
        // Cria um Map para representar o usuário
        Map<String, String> usuario = new HashMap<>();
        usuario.put("nome", name); // Armazena o nome do usuário no Map
    
        // Armazena o usuário na sessão
        model.addAttribute("usuario", usuario);
    
        // Redireciona para a página inicial
        return new ModelAndView("redirect:/index");
    }

    @GetMapping("/index")
    public ModelAndView index(@ModelAttribute("usuario") Map<String, String> usuario) {
        ModelAndView mv = new ModelAndView("index");

        // Verifica se o usuário está na sessão
        if (usuario != null && usuario.containsKey("nome")) {
            String nomeUsuario = usuario.get("nome");
            mv.addObject("userName", nomeUsuario);
        } else {
            mv.addObject("userName", "Visitante"); // Valor padrão se o usuário não estiver na sessão
        }

        // Gera 3 senhas diferentes para o CAPTCHA
        List<String> senhasCaptcha = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            senhasCaptcha.add(geradorSenha.GerarSenha());
        }
        mv.addObject("captchaOptions", senhasCaptcha);

        return mv;
    }

    @GetMapping("/captcha")
    public ModelAndView captcha(@RequestParam String captchaOption) {
        ModelAndView mv = new ModelAndView("captcha.html");
        mv.addObject("senha", captchaOption); // Passa a senha selecionada para a página
        captchaAtual = captchaOption; // Atualiza a variável captchaAtual com a opção selecionada
        return mv;
    }

    @GetMapping("/logout")
    public ModelAndView getMethodLogout() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");
        mv.addObject("logout", true);
        return mv;
    }

    @GetMapping("/bootstrap")
    public ModelAndView bootstrap() {
        return new ModelAndView("bootstrap.html");
    }

    @GetMapping("/info")
    public ModelAndView info() {
        return new ModelAndView("info.html");
    }
}