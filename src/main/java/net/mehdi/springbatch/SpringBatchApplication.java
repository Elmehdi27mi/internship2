package net.mehdi.springbatch;

import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
import net.mehdi.springbatch.repos.IctEncoursBrutRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class SpringBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchApplication.class, args);
	}
	@Bean
	CommandLineRunner runner(IctEncoursBrutRepository ictEncoursBrutRepository , IctEncoursBrutMapper mapper){
		return args -> {
			List<IctEncoursBrut> all = ictEncoursBrutRepository.findAll();
			all.forEach(
					a-> {
						IctEncoursBrutDto ictEncoursBrutDto =mapper.entityToDto(a);
						System.out.println("****************************************************************");
						System.out.println(ictEncoursBrutDto);
					}
			);
		};
	}
}
