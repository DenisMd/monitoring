package ru.monitor.Mining;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class MiningApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiningApplication.class, args);

//		Components components = JSensors.get.components();
//
//		List<Gpu> gpus = components.gpus;
//		for(Gpu gpu: gpus) {
//			System.out.println(gpu.name);
//			List<Load> loads = gpu.sensors.loads;
//			for(Load load: loads) {
//				System.out.println(load.value);
//			}
//
//			System.out.println("---------------------");
//
//			List<Temperature> temperatures = gpu.sensors.temperatures;
//			for (Temperature temperature: temperatures) {
//
//				System.out.println(temperature.value);
//			}
//
//		}
	}


}
