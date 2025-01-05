package edu.dcc192.ex03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController { 

    @Autowired
    private GeradorSenha geradorSenha;

    private String captchaAtual;

    @RequestMapping("/")
    public ModelAndView home(){
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

    @GetMapping("/menu")
    public ModelAndView getMethodName(@RequestParam(required = false) String name) {
        ModelAndView mv = new ModelAndView("index");
        if (name != null) {
            mv.addObject("userName", name); // Passa o nome como parâmetro para a página
        }
        // Gera 3 senhas diferentes para o CAPTCHA
        List<String> senhasCaptcha = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            senhasCaptcha.add(geradorSenha.GerarSenha());
        }
        mv.addObject("captchaOptions", senhasCaptcha); // Passa as senhas para a página
        return mv;
    }

    @GetMapping("captcha")
    public ModelAndView captcha(@RequestParam String captchaOption) {
        ModelAndView mv = new ModelAndView("captcha.html");
        mv.addObject("senha", captchaOption); // Passa a senha selecionada para a página
        captchaAtual = captchaOption; // Atualiza a variável captchaAtual com a opção selecionada
        return mv;
    }

    @GetMapping("logout")
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

    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("index");
        return mv;
    }
}