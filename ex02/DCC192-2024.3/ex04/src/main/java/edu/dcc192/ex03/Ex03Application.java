package edu.dcc192.ex03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Ex03Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Ex03Application.class, args);
	
	UsuarioRepository rep = ctx.getBean(UsuarioRepository.class);
		rep.save(new Usuario("ciro","1234"));
		rep.save(new Usuario("jose","1234"));
		rep.save(new Usuario("maria","1234"));

		System.out.println(rep.findAll().toString());
	}
}
