package edu.dcc192.ex03;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura erros 404 (Página não encontrada)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView erro404(NoHandlerFoundException ex, Model model) {
        model.addAttribute("errorMessage", 
            "Por favor, contacte o suporte e informe o erro.\n\nMensagem de erro: No endpoint GET " + ex.getRequestURL());
        return new ModelAndView("error");
    }

    // Captura erros de Java (RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView erroJava(RuntimeException ex, Model model) {
        model.addAttribute("errorMessage", 
            "Por favor, contacte o suporte e informe o erro.\n\nMensagem de erro: Erro no tratamento de dados.");
        return new ModelAndView("error");
    }
}